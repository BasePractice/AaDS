package ru.mifi.practice.vol92.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Сравнение: один поток против пула на независимых кусках работы. */
public final class PoolBenchmark {
    public long single(int chunks) {
        long total = 0;
        for (int chunk = 0; chunk < chunks; chunk++) {
            total += work(chunk);
        }
        return total;
    }

    public long pooled(int chunks, int threads) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            long total = 0;
            for (int chunk = 0; chunk < chunks; chunk++) {
                final int work = chunk;
                Future<Long> future = pool.submit(() -> work(work));
                total += future.get();
            }
            return total;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (java.util.concurrent.ExecutionException e) {
            throw new IllegalStateException("chunk failed", e);
        } finally {
            pool.shutdown();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private long work(int chunk) {
        long sum = 0;
        for (int i = 0; i < 1_000_000; i++) {
            sum += (long) i * chunk;
        }
        return sum;
    }
}
