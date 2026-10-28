package ru.mifi.practice.voln.slab;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Slab-кэш Бонвика: распределитель объектов одного размера поверх страничной памяти. Каждая
 * страница, взятая у памяти, становится плитой и режется на объекты; объект выдаётся и
 * возвращается за O(1), без поиска подходящего куска и без заголовка перед каждым объектом.
 * Внешней фрагментации нет вовсе: все дыры в плите одного размера и подходят любому запросу.
 *
 * <p>Плиты делятся на три списка: полные, частично занятые и пустые. Выдача берёт объект сперва из
 * частичной плиты, затем из пустой и лишь потом просит у памяти новую страницу. Так живые объекты
 * сбиваются в немногие плиты, а остальные пустеют целиком. Пустую плиту кэш не отдаёт сразу —
 * следующая выдача обошлась бы новым запросом страницы. Вернуть их памяти можно явно, через
 * shrink(), когда памяти начинает не хватать.
 *
 * <p>Плита по адресу ищется делением на размер страницы, как в ядре, где страница знает свою
 * плиту. Полный список не хранится: полная плита есть только в этом отображении.
 *
 * <p>В статье Бонвика кэш ещё и хранит объекты сконструированными, чтобы не платить за
 * инициализацию при каждой выдаче, и сдвигает начало плит («раскраска»), чтобы объекты разных
 * плит не делили строки кэша процессора. Здесь память — голые байты, и обе идеи опущены.
 */
public interface Cache {
    int allocate();

    void free(int address);

    void shrink();

    int slabs();

    final class Slabs implements Cache {
        private final Memory memory;
        private final int size;
        private final Map<Integer, Slab> slabs;
        private final Set<Slab> partial;
        private final Set<Slab> empty;

        public Slabs(Memory memory, int size) {
            this.memory = memory;
            this.size = size(memory, size);
            this.slabs = new HashMap<>();
            this.partial = new LinkedHashSet<>();
            this.empty = new LinkedHashSet<>();
        }

        @Override
        public int allocate() {
            Slab slab = partial.stream().findFirst().or(() -> empty.stream().findFirst()).orElseGet(this::grown);
            empty.remove(slab);
            int address = slab.allocate();
            if (slab.full()) {
                partial.remove(slab);
            } else {
                partial.add(slab);
            }
            return address;
        }

        @Override
        public void free(int address) {
            Slab slab = slabs.get(Math.floorDiv(address, memory.size()));
            if (slab == null) {
                throw new IllegalArgumentException("Адрес " + address + " лежит вне плит этого кэша");
            }
            slab.free(address);
            if (slab.empty()) {
                partial.remove(slab);
                empty.add(slab);
            } else {
                partial.add(slab);
            }
        }

        @Override
        public void shrink() {
            for (Slab slab : empty) {
                slabs.remove(slab.page());
                memory.release(slab.page());
            }
            empty.clear();
        }

        @Override
        public int slabs() {
            return slabs.size();
        }

        private Slab grown() {
            Slab slab = new Slab(memory, memory.page(), size);
            slabs.put(slab.page(), slab);
            return slab;
        }

        private int size(Memory memory, int size) {
            if (size < Integer.BYTES) {
                throw new IllegalArgumentException("Объект в " + size + " байт не вмещает ссылку на следующий свободный");
            }
            if (size > memory.size()) {
                throw new IllegalArgumentException("Объект в " + size + " байт не помещается в страницу из " + memory.size());
            }
            return size;
        }
    }
}
