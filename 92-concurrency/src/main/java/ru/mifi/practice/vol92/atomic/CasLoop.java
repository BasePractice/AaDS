package ru.mifi.practice.vol92.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/** Цикл compare-and-set: повторяем, пока между чтением и записью вмешался чужой поток. */
public final class CasLoop {
    private final AtomicInteger value;

    public CasLoop() {
        this.value = new AtomicInteger();
    }

    public void advance(int times) {
        for (int i = 0; i < times; i++) {
            int current;
            do {
                current = value.get();
            } while (!value.compareAndSet(current, current + 1));
        }
    }

    public int value() {
        return value.get();
    }
}
