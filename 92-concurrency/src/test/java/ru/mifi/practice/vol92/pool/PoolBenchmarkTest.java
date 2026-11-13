package ru.mifi.practice.vol92.pool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Один поток против пула")
final class PoolBenchmarkTest {

    @DisplayName("Пул выдаёт ту же сумму, что один поток")
    @Test
    @Timeout(60)
    void givesTheSameTotalAsSingleThread() throws InterruptedException {
        PoolBenchmark benchmark = new PoolBenchmark();
        long single = benchmark.single(4);
        assertThat("pooled total differs from single-threaded total",
            benchmark.pooled(4, 4), is(single));
    }

    @DisplayName("Вторая задача стартует, пока первая ещё выполняется")
    @Test
    @Timeout(10)
    void startsBothTasksBeforeCollectingResults() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(2);
        long total = new PoolBenchmark().pooled(2, 2, chunk -> {
            started.countDown();
            try {
                return started.await(2, TimeUnit.SECONDS) ? chunk + 1L : 0L;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Работа прервана", e);
            }
        });
        assertThat("Две задачи выполнялись по очереди", total, is(3L));
    }
}
