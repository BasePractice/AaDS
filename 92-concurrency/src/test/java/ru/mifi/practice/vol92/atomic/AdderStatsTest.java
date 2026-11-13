package ru.mifi.practice.vol92.atomic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Статистика на LongAdder")
final class AdderStatsTest {

    @DisplayName("Два потока собирают точную статистику")
    @Test
    @Timeout(30)
    void keepsExactTotalFromTwoThreads() throws InterruptedException {
        AdderStats stats = new AdderStats();
        Thread first = new Thread(() -> stats.track(10_000));
        Thread second = new Thread(() -> stats.track(10_000));
        first.start();
        second.start();
        first.join();
        second.join();
        assertThat("long adder loses statistics under race",
            stats.total(), is(20_000L));
    }
}
