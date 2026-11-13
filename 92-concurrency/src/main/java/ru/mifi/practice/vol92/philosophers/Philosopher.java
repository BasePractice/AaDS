package ru.mifi.practice.vol92.philosophers;

/** Философ: циклы «подумать — взять вилки — поесть» по стратегии стола. */
public final class Philosopher implements Runnable {
    private final Table table;
    private final int seat;
    private final int cycles;

    public Philosopher(Table table, int seat, int cycles) {
        this.table = table;
        this.seat = seat;
        this.cycles = cycles;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < cycles; i++) {
                switch (table.strategy()) {
                    case GREEDY -> greedy();
                    case ORDERED -> ordered();
                    case SEMAPHORE -> limited();
                    case TRYLOCK -> opportunistic();
                    default -> throw new IllegalStateException("unknown strategy");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            table.finish();
        }
    }

    private void greedy() throws InterruptedException {
        table.barrier();
        table.fork(seat).grab();
        Thread.sleep(20);
        table.fork((seat + 1) % table.seats()).grab();
        eat();
        table.fork((seat + 1) % table.seats()).put();
        table.fork(seat).put();
    }

    private void ordered() throws InterruptedException {
        table.barrier();
        Fork first = table.fork(seat);
        Fork second = table.fork((seat + 1) % table.seats());
        if (second.index() < first.index()) {
            Fork swap = first;
            first = second;
            second = swap;
        }
        first.grab();
        second.grab();
        eat();
        second.put();
        first.put();
    }

    private void limited() throws InterruptedException {
        table.barrier();
        table.room().acquire();
        table.fork(seat).grab();
        table.fork((seat + 1) % table.seats()).grab();
        eat();
        table.fork((seat + 1) % table.seats()).put();
        table.fork(seat).put();
        table.room().release();
    }

    private void opportunistic() throws InterruptedException {
        Fork left = table.fork(seat);
        Fork right = table.fork((seat + 1) % table.seats());
        if (!left.take()) {
            return;
        }
        if (!right.take()) {
            left.put();
            return;
        }
        eat();
        right.put();
        left.put();
    }

    private void eat() throws InterruptedException {
        table.meal();
        Thread.sleep(1);
    }
}
