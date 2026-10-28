package ru.mifi.practice.vol1.deque;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.NoSuchElementException;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Дек")
final class RingDequeTest {

    @DisplayName("Новый дек пуст")
    @Test
    @Timeout(1)
    void startsEmpty() {
        assertThat("a fresh deque dont look empty", new RingDeque<Integer>(4).isEmpty(), is(true));
    }

    @DisplayName("Элемент, добавленный в начало, первым и извлекается из начала")
    @Test
    @Timeout(1)
    void servesTheFrontAsAStack() {
        Deque<Integer> deque = new RingDeque<>(4);
        deque.addFirst(1);
        deque.addFirst(2);
        assertThat("the front dont hold the latest addFirst", deque.removeFirst(), is(2));
    }

    @DisplayName("Добавление в конец и извлечение из начала работают как очередь")
    @Test
    @Timeout(1)
    void servesBackToFrontAsAQueue() {
        Deque<Integer> deque = new RingDeque<>(4);
        deque.addLast(1);
        deque.addLast(2);
        assertThat("addLast and removeFirst dont keep the arrival order", deque.removeFirst(), is(1));
    }

    @DisplayName("Добавление в начало и извлечение из конца работают как очередь")
    @Test
    @Timeout(1)
    void servesFrontToBackAsAQueue() {
        Deque<Integer> deque = new RingDeque<>(4);
        deque.addFirst(1);
        deque.addFirst(2);
        assertThat("addFirst and removeLast dont keep the arrival order", deque.removeLast(), is(1));
    }

    @DisplayName("Растёт за пределы начальной вместимости")
    @Test
    @Timeout(1)
    void growsPastTheInitialCapacity() {
        Deque<Integer> deque = new RingDeque<>(1);
        int count = 100 + new Random().nextInt(100);
        for (int i = 0; i < count; i++) {
            deque.addFirst(i);
        }
        assertThat("the deque loses elements while growing", deque.size(), is(count));
    }

    @DisplayName("Сохраняет порядок при росте через границу кольца")
    @Test
    @Timeout(1)
    void keepsTheOrderWhenGrowingAcrossTheRingBorder() {
        Deque<Integer> deque = new RingDeque<>(2);
        deque.addLast(2);
        deque.addFirst(1);
        deque.addLast(3);
        deque.removeFirst();
        deque.removeFirst();
        assertThat("growing across the ring border scrambles the order", deque.removeFirst(), is(3));
    }

    @DisplayName("Извлечение из пустого дека падает сразу")
    @Test
    @Timeout(1)
    void refusesToRemoveFromAnEmptyDeque() {
        assertThat("an empty deque dont refuse removal",
            assertThrows(NoSuchElementException.class, () -> new RingDeque<Integer>(2).removeLast()).getMessage(),
            is("Дек пуст"));
    }
}
