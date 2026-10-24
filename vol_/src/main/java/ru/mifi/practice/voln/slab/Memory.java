package ru.mifi.practice.voln.slab;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Память, нарезанная на страницы одного размера. Это нижний уровень распределения: он отдаёт и
 * забирает только целые страницы, как страничный распределитель ядра. Мелкие объекты из страниц
 * нарезает уже slab-кэш, а сама память о них ничего не знает.
 *
 * <p>Адрес — номер байта от начала памяти, страница с номером p начинается с адреса p * size().
 * Освобождённая страница уходит в голову стопки свободных и выдаётся следующей: недавно
 * тронутая память дешевле, потому что ещё лежит в кэше процессора.
 */
public interface Memory {
    int page();

    void release(int page);

    int size();

    int read(int address);

    void write(int address, int value);

    final class Arena implements Memory {
        private final ByteBuffer bytes;
        private final int size;
        private final Deque<Integer> free;
        private final BitSet taken;

        public Arena(int pages, int size) {
            this.bytes = ByteBuffer.allocate(capacity(pages, size));
            this.size = size;
            this.free = IntStream.range(0, pages).boxed().collect(Collectors.toCollection(ArrayDeque::new));
            this.taken = new BitSet(pages);
        }

        @Override
        public int page() {
            if (free.isEmpty()) {
                throw new IllegalStateException("Все " + taken.cardinality() + " страниц памяти уже выданы");
            }
            int page = free.pop();
            taken.set(page);
            return page;
        }

        @Override
        public void release(int page) {
            if (page < 0 || !taken.get(page)) {
                throw new IllegalArgumentException("Страница " + page + " не выдана и вернуть её нельзя");
            }
            taken.clear(page);
            free.push(page);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int read(int address) {
            return bytes.getInt(address);
        }

        @Override
        public void write(int address, int value) {
            bytes.putInt(address, value);
        }

        private int capacity(int pages, int size) {
            if (pages <= 0 || size <= 0) {
                throw new IllegalArgumentException("Память из " + pages + " страниц по " + size + " байт пуста");
            }
            return Math.multiplyExact(pages, size);
        }
    }
}
