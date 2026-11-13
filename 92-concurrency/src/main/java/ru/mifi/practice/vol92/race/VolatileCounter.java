package ru.mifi.practice.vol92.race;

/** Счётчик на volatile: видимость есть, атомарности нет — гонка остаётся. */
@SuppressWarnings("PMD.AvoidUsingVolatile")
public final class VolatileCounter implements Runnable {
    private final int steps;
    private volatile int value;

    public VolatileCounter(int steps) {
        this.steps = steps;
    }

    @Override
    public void run() {
        for (int i = 0; i < steps; i++) {
            value++;
        }
    }

    public int value() {
        return value;
    }
}
