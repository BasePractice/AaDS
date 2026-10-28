package ru.mifi.practice.voln.buffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка кольцевого буфера: порядок выдачи и затирание старейшего при переполнении. */
@DisplayName("Кольцевой буфер")
final class CircularBufferTest {

    @DisplayName("Новый буфер пуст")
    @Test
    @Timeout(1)
    void startsEmpty() {
        assertThat("a fresh buffer is not empty", new CircularBuffer.Default<Integer>(3).isEmpty(), is(true));
    }

    @DisplayName("Элементы выдаются в порядке добавления")
    @Test
    @Timeout(1)
    void givesBackInTheOrderOfAdding() {
        CircularBuffer<Integer> buffer = new CircularBuffer.Default<>(3);
        buffer.add(1);
        buffer.add(2);
        buffer.take();
        assertThat("the buffer dont give back in the order of adding", buffer.take().orElseThrow(), is(2));
    }

    @DisplayName("Переполнение затирает старейший элемент")
    @Test
    @Timeout(1)
    void overwritesTheOldestOnOverflow() {
        CircularBuffer<Integer> buffer = new CircularBuffer.Default<>(2);
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        assertThat("overflow dont overwrite the oldest item", buffer.take().orElseThrow(), is(2));
    }

    @DisplayName("Переполнение не раздувает размер буфера")
    @Test
    @Timeout(1)
    void keepsTheSizeWithinTheCapacity() {
        CircularBuffer<Integer> buffer = new CircularBuffer.Default<>(2);
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        assertThat("overflow grows the buffer beyond its capacity", buffer.size(), is(2));
    }

    @DisplayName("Взгляд не забирает элемент")
    @Test
    @Timeout(1)
    void keepsTheItemOnPeek() {
        CircularBuffer<Integer> buffer = new CircularBuffer.Default<>(3);
        buffer.add(1);
        buffer.peek();
        assertThat("peeking takes the item away", buffer.size(), is(1));
    }

    @DisplayName("Из пустого буфера ничего не берётся")
    @Test
    @Timeout(1)
    void givesNothingFromAnEmptyBuffer() {
        assertThat("an empty buffer gives an item",
            new CircularBuffer.Default<Integer>(3).take().isPresent(), is(false));
    }

    @DisplayName("Очистка опустошает буфер")
    @Test
    @Timeout(1)
    void emptiesOnClear() {
        CircularBuffer<Integer> buffer = new CircularBuffer.Default<>(3);
        buffer.add(1);
        buffer.clear();
        assertThat("clearing dont empty the buffer", buffer.isEmpty(), is(true));
    }

    @DisplayName("Буфер переживает много кругов подряд")
    @Test
    @Timeout(1)
    void survivesManyLapsInARow() {
        CircularBuffer<Integer> buffer = new CircularBuffer.Default<>(3);
        for (int step = 0; step < 100; step++) {
            buffer.add(step);
            buffer.take();
        }
        assertThat("the buffer dont survive many laps in a row", buffer.isEmpty(), is(true));
    }
}
