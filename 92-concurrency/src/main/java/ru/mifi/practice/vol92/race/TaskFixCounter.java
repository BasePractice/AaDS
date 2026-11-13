package ru.mifi.practice.vol92.race;

/**
 * Место для решения студента: сделайте счётчик потокобезопасным
 * и получите 2 * steps после двух потоков.
 */
public final class TaskFixCounter implements Runnable {
    private final int steps;
    private int value;

    public TaskFixCounter(int steps) {
        this.steps = steps;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Место для решения студента: " + steps + " шагов");
    }

    public int value() {
        return value;
    }
}
