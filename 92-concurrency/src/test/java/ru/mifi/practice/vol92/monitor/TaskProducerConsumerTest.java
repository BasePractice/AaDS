package ru.mifi.practice.vol92.monitor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DisplayName("Задание: конвейер производителя и потребителя")
final class TaskProducerConsumerTest {

    @Disabled("Место для решения студента")
    @DisplayName("pipe перекачивает сообщения из очереди в список")
    @Test
    @Timeout(30)
    void pipesMessagesFromQueueToList() throws InterruptedException {
        ProducerConsumer queue = new ProducerConsumer(4);
        for (int i = 0; i < 5; i++) {
            queue.put("сообщение " + i);
        }
        assertThat("student pipe loses messages",
            new TaskProducerConsumer().pipe(queue, 5), hasSize(5));
    }
}
