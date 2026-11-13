package ru.mifi.practice.vol92.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Корректное завершение пула: shutdown и awaitTermination. */
public final class PoolShutdown {
    private final ExecutorService pool;

    public PoolShutdown(int threads) {
        this.pool = Executors.newFixedThreadPool(threads);
    }

    public Future<Integer> task(int value) {
        return pool.submit(() -> value * 2);
    }

    public boolean finish() throws InterruptedException {
        pool.shutdown();
        return pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
