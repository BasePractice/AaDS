package ru.mifi.practice.vol92.thread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@DisplayName("Задача с остановкой по interrupt")
final class InterruptibleTaskTest {

    @DisplayName("Поток останавливается по interrupt")
    @Test
    @Timeout(10)
    void stopsOnInterrupt() throws InterruptedException {
        List<String> journal = new CopyOnWriteArrayList<>();
        Thread worker = new Thread(new InterruptibleTask(journal));
        worker.start();
        Thread.sleep(150);
        worker.interrupt();
        worker.join(1000);
        assertThat("interrupted task keeps running",
            journal, hasItem("остановлен"));
    }
}
