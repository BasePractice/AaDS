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
        boolean completed = false;
        try {
            for (Thread thread : threads) {
                thread.start();
            }
            completed = done.await(timeout, unit);
        } finally {
            joinWorkers(threads, completed);
        }
        return meals.get();
    }

    private static void joinWorkers(Thread[] threads, boolean completed) throws InterruptedException {
        if (!completed) {
            interruptWorkers(threads);
        }
        InterruptedException interrupted = null;
        for (Thread thread : threads) {
            while (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    interrupted = e;
                    interruptWorkers(threads);
                }
            }
        }
        if (interrupted != null) {
            throw interrupted;
        }
    }

    private static void interruptWorkers(Thread[] threads) {
        for (Thread thread : threads) {
            thread.interrupt();
        }
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
            InterruptedException interrupted = new InterruptedException("Ожидание прервано у другого философа");
            interrupted.initCause(e);
            throw interrupted;
        }
    }

    Semaphore room() {
        return room;
    }
}
