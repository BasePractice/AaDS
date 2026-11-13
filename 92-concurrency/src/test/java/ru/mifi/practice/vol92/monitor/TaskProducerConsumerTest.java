package ru.mifi.practice.vol92.monitor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Задание: конвейер производителя и потребителя")
final class TaskProducerConsumerTest {

    @Disabled("Место для решения студента")
    @DisplayName("pipe перекачивает сообщения из очереди в список")
    @Test
    @Timeout(30)
    void pipesMessagesFromQueueToList() throws Exception {
        ProducerConsumer queue = new ProducerConsumer(4);
        List<String> messages = List.of("первое", "второе", "третье", "четвёртое", "пятое");
        FutureTask<Void> send = new FutureTask<>(() -> {
            for (String message : messages) {
                queue.put(message);
            }
            return null;
        });
        Thread producer = new Thread(send);
        producer.start();
        try {
            assertThat("Нарушен порядок сообщений",
                new TaskProducerConsumer().pipe(queue, messages.size()), is(messages));
            send.get(5, TimeUnit.SECONDS);
        } finally {
            send.cancel(true);
            producer.join();
        }
    }
}
