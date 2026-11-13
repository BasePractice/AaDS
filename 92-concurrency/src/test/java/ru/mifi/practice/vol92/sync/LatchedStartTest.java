package ru.mifi.practice.vol92.sync;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Стартовый затвор")
final class LatchedStartTest {

    @DisplayName("Потоки стартуют только после щелчка")
    @Test
    @Timeout(30)
    void startsThreadsOnlyAfterClick() throws InterruptedException {
        LatchedStart start = new LatchedStart();
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        Thread.sleep(100);
        assertThat("threads start before the latch click",
            start.started(), is(0));
        start.click();
        for (Thread thread : threads) {
            thread.join(5000);
        }
        assertThat("threads dont start after the latch click",
            start.started(), is(4));
    }
}
