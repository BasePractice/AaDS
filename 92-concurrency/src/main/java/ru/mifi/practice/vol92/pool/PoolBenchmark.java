package ru.mifi.practice.vol92.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntToLongFunction;

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
        return pooled(chunks, threads, this::work);
    }

    long pooled(int chunks, int threads, IntToLongFunction calculate) throws InterruptedException {
        List<Callable<Long>> tasks = new ArrayList<>();
        for (int chunk = 0; chunk < chunks; chunk++) {
            final int index = chunk;
            tasks.add(() -> calculate.applyAsLong(index));
        }
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            long total = 0;
            for (Future<Long> future : pool.invokeAll(tasks)) {
                total += future.get();
            }
            return total;
        } catch (ExecutionException e) {
            throw new IllegalStateException("Ошибка при расчёте части работы", e);
        } finally {
            pool.shutdownNow();
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
