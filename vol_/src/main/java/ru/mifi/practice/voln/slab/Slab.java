package ru.mifi.practice.voln.slab;

import java.util.BitSet;

/**
 * Плита — одна страница, нарезанная на объекты одного размера. Список свободных объектов живёт в
 * них самих: в первых четырёх байтах свободного объекта записан номер следующего свободного, а
 * последний хранит -1. Учёт свободного места поэтому не стоит ни байта сверх страницы, а выдача и
 * возврат объекта — снятие и вставка в голову этого списка за O(1).
 *
 * <p>Пока объект выдан, его байты принадлежат владельцу и ссылка в них затирается; восстанавливается
 * она при возврате. Битовая карта выданных объектов к алгоритму не относится: она ловит повторное
 * освобождение сразу, а не через испорченный список, который всплыл бы много позже.
 */
final class Slab {
    private static final int END = -1;
    private final Memory memory;
    private final int base;
    private final int size;
    private final BitSet used;
    private int head;

    Slab(Memory memory, int page, int size) {
        this.memory = memory;
        this.base = page * memory.size();
        this.size = size;
        this.used = new BitSet(memory.size() / size);
        this.head = link(memory.size() / size);
    }

    int allocate() {
        if (full()) {
            throw new IllegalStateException("Плита на странице " + page() + " заполнена, свободных объектов в ней нет");
        }
        int index = head;
        head = memory.read(base + index * size);
        used.set(index);
        return base + index * size;
    }

    void free(int address) {
        int offset = address - base;
        if (offset < 0 || offset % size != 0 || offset / size >= memory.size() / size) {
            throw new IllegalArgumentException("Адрес " + address + " не указывает на начало объекта плиты");
        }
        if (!used.get(offset / size)) {
            throw new IllegalArgumentException("Объект по адресу " + address + " уже свободен");
        }
        used.clear(offset / size);
        memory.write(address, head);
        head = offset / size;
    }

    boolean full() {
        return head == END;
    }

    boolean empty() {
        return used.isEmpty();
    }

    int page() {
        return base / memory.size();
    }

    private int link(int count) {
        for (int index = 0; index < count; index++) {
            memory.write(base + index * size, index + 1 < count ? index + 1 : END);
        }
        return 0;
    }
}
