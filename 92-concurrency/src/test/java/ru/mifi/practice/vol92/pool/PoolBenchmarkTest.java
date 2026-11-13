package ru.mifi.practice.vol92.pool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

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
}
