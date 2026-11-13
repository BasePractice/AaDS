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
        Fork left = table.fork(seat);
        left.grab();
        try {
            // Каждый удерживает свою вилку, прежде чем пытаться взять соседнюю.
            table.barrier();
            eatWithFork(table.fork((seat + 1) % table.seats()));
        } finally {
            left.put();
        }
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
        eatWithForks(first, second);
    }

    private void limited() throws InterruptedException {
        table.barrier();
        table.room().acquire();
        try {
            eatWithForks(table.fork(seat), table.fork((seat + 1) % table.seats()));
        } finally {
            table.room().release();
        }
    }

    private void opportunistic() throws InterruptedException {
        Fork left = table.fork(seat);
        Fork right = table.fork((seat + 1) % table.seats());
        if (!left.take()) {
            return;
        }
        try {
            if (right.take()) {
                try {
                    eat();
                } finally {
                    right.put();
                }
            }
        } finally {
            left.put();
        }
    }

    private void eatWithForks(Fork first, Fork second) throws InterruptedException {
        first.grab();
        try {
            eatWithFork(second);
        } finally {
            first.put();
        }
    }

    private void eatWithFork(Fork fork) throws InterruptedException {
        fork.grab();
        try {
            eat();
        } finally {
            fork.put();
        }
    }

    private void eat() throws InterruptedException {
        table.meal();
        Thread.sleep(1);
    }
}
