package ru.mifi.practice.vol92.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Future: отправляем работу в пул и забираем результат блокирующим get. */
public final class SumFuture {
    private final ExecutorService pool;

    public SumFuture() {
        this.pool = Executors.newSingleThreadExecutor();
    }

    public Future<Long> sum(int to) {
        return pool.submit(() -> {
            long total = 0;
            for (int i = 1; i <= to; i++) {
                total += i;
            }
            return total;
        });
    }

    public boolean finish() throws InterruptedException {
        pool.shutdown();
        return pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
