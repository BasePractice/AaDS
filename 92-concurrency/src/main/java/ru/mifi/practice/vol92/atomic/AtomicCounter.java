package ru.mifi.practice.vol92.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/** Счётчик на AtomicInteger: инкремент атомарный, гонки нет. */
public final class AtomicCounter implements Runnable {
    private final AtomicInteger value;
    private final int steps;

    public AtomicCounter(int steps) {
        this.value = new AtomicInteger();
        this.steps = steps;
    }

    @Override
    public void run() {
        for (int i = 0; i < steps; i++) {
            value.incrementAndGet();
        }
    }

    public int value() {
        return value.get();
    }
}
