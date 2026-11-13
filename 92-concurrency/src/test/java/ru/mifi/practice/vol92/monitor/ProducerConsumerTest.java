package ru.mifi.practice.vol92.monitor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Кольцевой буфер на мониторе")
final class ProducerConsumerTest {

    @DisplayName("Производитель и потребитель обмениваются сообщениями через wait/notifyAll")
    @Test
    @Timeout(30)
    void exchangesMessagesBetweenProducerAndConsumer() throws InterruptedException {
        ProducerConsumer queue = new ProducerConsumer(2);
        List<String> taken = new ArrayList<>();
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    taken.add(queue.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        queue.put("первый");
        queue.put("второй");
        queue.put("третий");
        consumer.join(5000);
        assertThat("consumer missed messages from the monitor buffer",
            taken, contains("первый", "второй", "третий"));
        assertThat("buffer keeps messages after consumption",
            queue.size(), is(0));
    }

    @DisplayName("Буфер на две ячейки не теряет сообщения при переполнении")
    @Test
    @Timeout(30)
    void waitsWhenBufferIsFull() throws InterruptedException {
        ProducerConsumer queue = new ProducerConsumer(2);
        Thread producer = new Thread(() -> {
            try {
                queue.put("а");
                queue.put("б");
                queue.put("в");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        Thread.sleep(50);
        assertThat("producer overflows the buffer without a waiting consumer",
            queue.size(), is(2));
        assertThat("message order breaks in the ring buffer",
            queue.take(), is("а"));
    }
}
