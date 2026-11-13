package ru.mifi.practice.vol92.philosophers;

/**
 * Место для решения студента: верните Table со стратегией,
 * при которой жадные философы не попадают в дедлок.
 */
public final class TaskNoDeadlock {
    private final int seats;
    private final int cycles;

    public TaskNoDeadlock(int seats, int cycles) {
        this.seats = seats;
        this.cycles = cycles;
    }

    public Table table() {
        throw new UnsupportedOperationException("Место для решения студента");
    }

    public int seats() {
        return seats;
    }

    public int cycles() {
        return cycles;
    }
}
