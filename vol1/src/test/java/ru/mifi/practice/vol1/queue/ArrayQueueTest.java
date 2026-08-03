package ru.mifi.practice.vol1.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol1.array.LinkedArray;
import ru.mifi.practice.vol1.array.StandardArray;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Очередь")
final class ArrayQueueTest {

    @DisplayName("Новая очередь пуста")
    @Test
    void startsEmpty() {
        assertThat("a fresh queue dont look empty",
            new ArrayQueue<>(new StandardArray<Integer>(4)).isEmpty(), is(true));
    }

    @DisplayName("После добавления очередь не пуста")
    @Test
    void stopsBeingEmptyOnEnqueue() {
        Queue<Integer> queue = new ArrayQueue<>(new StandardArray<>(4));
        queue.enqueue(1);
        assertThat("queue still looks empty after enqueue", queue.isEmpty(), is(false));
    }

    @DisplayName("Извлекает элементы в порядке добавления")
    @Test
    void servesElementsInArrivalOrder() {
        Queue<Integer> queue = new ArrayQueue<>(new StandardArray<>(4));
        queue.enqueue(1);
        queue.enqueue(2);
        queue.dequeue();
        assertThat("queue dont serve the oldest element first", queue.dequeue(), is(2));
    }

    @DisplayName("Извлечение опустошает очередь")
    @Test
    void becomesEmptyWhenEveryElementIsServed() {
        Queue<Integer> queue = new ArrayQueue<>(new StandardArray<>(4));
        queue.enqueue(1);
        queue.enqueue(2);
        queue.dequeue();
        queue.dequeue();
        assertThat("queue dont become empty after every element is served", queue.isEmpty(), is(true));
    }

    @DisplayName("Работает поверх связного списка")
    @Test
    @Timeout(1)
    void servesElementsInArrivalOrderOnLinkedStorage() {
        Queue<Integer> queue = new ArrayQueue<>(new LinkedArray<>());
        queue.enqueue(1);
        queue.enqueue(2);
        queue.dequeue();
        assertThat("linked storage dont keep the arrival order", queue.dequeue(), is(2));
    }
}
