package ru.mifi.practice.vol92.philosophers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Стол на seats философов с выбранной стратегией взятия вилок. */
public final class Table {
    public enum Strategy {
        GREEDY,
        ORDERED,
        SEMAPHORE,
        TRYLOCK
    }

    private final Strategy strategy;
    private final int cycles;
    private final Fork[] forks;
    private final AtomicInteger meals;
    private final CountDownLatch done;
    private final Semaphore room;
    private final CyclicBarrier barrier;

    public Table(int seats, Strategy strategy, int cycles) {
        this.strategy = strategy;
        this.cycles = cycles;
        this.forks = new Fork[seats];
        for (int i = 0; i < seats; i++) {
            forks[i] = new Fork(i);
        }
        this.meals = new AtomicInteger();
        this.done = new CountDownLatch(seats);
        this.room = new Semaphore(seats - 1);
        this.barrier = new CyclicBarrier(seats);
    }

    /** Прогон: возвращает число приёмов пищи; зависшие потоки прерываются по таймауту. */
    public int dine(long timeout, TimeUnit unit) throws InterruptedException {
        Thread[] threads = new Thread[forks.length];
        for (int seat = 0; seat < forks.length; seat++) {
            threads[seat] = new Thread(new Philosopher(this, seat, cycles));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        if (!done.await(timeout, unit)) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
            done.await(1, TimeUnit.SECONDS);
        }
        for (Thread thread : threads) {
            thread.join(1000);
        }
        return meals.get();
    }

    public int expected() {
        return forks.length * cycles;
    }

    Strategy strategy() {
        return strategy;
    }

    int seats() {
        return forks.length;
    }

    Fork fork(int index) {
        return forks[index];
    }

    void meal() {
        meals.incrementAndGet();
    }

    void finish() {
        done.countDown();
    }

    void barrier() throws InterruptedException {
        try {
            barrier.await();
        } catch (java.util.concurrent.BrokenBarrierException e) {
            throw new IllegalStateException("barrier broken", e);
        }
    }

    Semaphore room() {
        return room;
    }
}
