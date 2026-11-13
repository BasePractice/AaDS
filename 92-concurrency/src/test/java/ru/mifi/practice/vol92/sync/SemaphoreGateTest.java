package ru.mifi.practice.vol92.sync;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Комната с семафором")
final class SemaphoreGateTest {

    @DisplayName("Не больше трёх потоков входит в комнату")
    @Test
    @Timeout(30)
    void admitsNoMoreThanThreeThreads() throws InterruptedException {
        SemaphoreGate gate = new SemaphoreGate(3);
        Thread[] threads = new Thread[6];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    gate.enter();
                    TimeUnit.MILLISECONDS.sleep(50);
                    gate.leave();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join(5000);
        }
        assertThat("more than three threads enter the gated room at once",
            gate.peak(), is(3));
    }
}
