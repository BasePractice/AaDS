package ru.mifi.practice.vol92.atomic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Счётчик на AtomicInteger")
final class AtomicCounterTest {

    @DisplayName("Два потока досчитывают без потерь")
    @Test
    @Timeout(30)
    void keepsAllIncrementsFromTwoThreads() throws InterruptedException {
        int steps = 100_000;
        AtomicCounter counter = new AtomicCounter(steps);
        Thread first = new Thread(counter);
        Thread second = new Thread(counter);
        first.start();
        second.start();
        first.join();
        second.join();
        assertThat("atomic counter loses increments under race",
            counter.value(), is(2 * steps));
    }
}
