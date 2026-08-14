package ru.mifi.practice.boring.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.NoSuchElementException;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Биномиальная куча")
final class BinomialHeapTest {

    @DisplayName("В вершине лежит наименьшее число")
    @Test
    @Timeout(1)
    void keepsTheSmallestOnTop() {
        BinomialHeap heap = new BinomialHeap();
        for (int value : new int[]{5, 3, 9, 1}) {
            heap.add(value);
        }
        assertThat("the smallest value is not on top", heap.peek(), is(1));
    }

    @DisplayName("Отдаёт числа по возрастанию")
    @Test
    @Timeout(1)
    void givesTheValuesInOrder() {
        BinomialHeap heap = new BinomialHeap();
        for (int value : new int[]{5, 3, 9, 1}) {
            heap.add(value);
        }
        assertThat("the values leave the heap out of order",
            "" + heap.poll() + heap.poll() + heap.poll() + heap.poll(), is("1359"));
    }

    @DisplayName("Считает добавленные числа")
    @Test
    @Timeout(1)
    void countsTheAddedValues() {
        BinomialHeap heap = new BinomialHeap();
        for (int value : new int[]{5, 3, 9, 1, 7}) {
            heap.add(value);
        }
        assertThat("the heap miscounts its values", heap.size(), is(5));
    }

    @DisplayName("Слияние забирает числа второй кучи")
    @Test
    @Timeout(1)
    void takesTheValuesOfTheOtherHeap() {
        BinomialHeap heap = new BinomialHeap();
        BinomialHeap other = new BinomialHeap();
        heap.add(5);
        other.add(3);
        other.add(1);
        heap.merge(other);
        assertThat("the merged heap lost the values of the other one", heap.size(), is(3));
    }

    @DisplayName("После слияния в вершине наименьшее из обеих куч")
    @Test
    @Timeout(1)
    void keepsTheSmallestOfBothHeapsOnTop() {
        BinomialHeap heap = new BinomialHeap();
        BinomialHeap other = new BinomialHeap();
        heap.add(5);
        other.add(1);
        heap.merge(other);
        assertThat("the smallest value of the other heap is lost", heap.peek(), is(1));
    }

    @DisplayName("Слитая куча пустеет")
    @Test
    @Timeout(1)
    void emptiesTheMergedHeap() {
        BinomialHeap heap = new BinomialHeap();
        BinomialHeap other = new BinomialHeap();
        other.add(1);
        heap.merge(other);
        assertThat("the merged heap keeps its values", other.isEmpty(), is(true));
    }

    @DisplayName("Пустая куча ничего не отдаёт")
    @Test
    @Timeout(1)
    void refusesToPollWhenEmpty() {
        assertThat("the empty heap gives away a value",
            assertThrows(NoSuchElementException.class, () -> new BinomialHeap().poll()).getMessage(),
            is("Куча пуста"));
    }

    @DisplayName("Отдаёт числа по возрастанию на случайном входе")
    @Test
    @Timeout(10)
    void givesRandomValuesInOrder() {
        Random random = new Random(20260815L);
        BinomialHeap heap = new BinomialHeap();
        for (int i = 0; i < 2000; i++) {
            heap.add(random.nextInt(10_000));
        }
        int previous = Integer.MIN_VALUE;
        int violations = 0;
        while (!heap.isEmpty()) {
            int value = heap.poll();
            violations += value >= previous ? 0 : 1;
            previous = value;
        }
        assertThat("the heap gives the random values out of order", violations, is(0));
    }
}
