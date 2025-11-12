package ru.mifi.practice.vol1.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mifi.practice.vol1.array.StandardArray;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Очередь")
class ArrayQueueTest {

    private ArrayQueue<Integer> queue;

    @BeforeEach
    void setUp() {
        queue = new ArrayQueue<>(new StandardArray<>(100));
    }

    @DisplayName("Добавляем элемент")
    @Test
    void enqueue() {

    }

    @DisplayName("Удаляем элемент")
    @Test
    void dequeue() {
    }

    @DisplayName("Проверяем на пустоту")
    @Test
    void isEmpty() {
        assertTrue(queue.isEmpty());
        queue.enqueue(1);
        assertFalse(queue.isEmpty());
    }
}
