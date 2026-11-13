package ru.mifi.practice.vol92.patterns;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Счётчик на обмене сообщениями")
final class MessagePassingCounterTest {

    @DisplayName("Два потока передают сообщения и счётчик сходится")
    @Test
    @Timeout(30)
    void collectsExactTotalFromTwoSenders() throws InterruptedException {
        MessagePassingCounter counter = new MessagePassingCounter(0);
        Thread first = new Thread(() -> {
            try {
                counter.send(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread second = new Thread(() -> {
            try {
                counter.send(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        assertThat("message passing loses counts",
            counter.collect(2), is(200));
    }
}
