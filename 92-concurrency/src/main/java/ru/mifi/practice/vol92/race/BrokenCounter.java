package ru.mifi.practice.vol92.race;

/** Счётчик с гонкой данных: инкремент — чтение, сложение и запись в три шага. */
public final class BrokenCounter implements Runnable {
    private final int steps;
    private int value;

    public BrokenCounter(int steps) {
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
