package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка двоичной кучи со штатной приоритетной очередью на случайных входах.
 *
 * <p>Диапазон значений узкий, чтобы повторы попадались часто: на равных ключах ломается
 * погружение, если сравнение перепутано со строгим.
 */
@DisplayName("Двоичная куча на случайных входах")
final class HeapRandomTest {

    @DisplayName("Куча отдаёт элементы по возрастанию, как штатная очередь")
    @Test
    @Timeout(10)
    void drainsInTheSameOrderAsTheStandardQueue() {
        Random random = new Random(20260804L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < 1 + random.nextInt(30); i++) {
                values.add(random.nextInt(20));
            }
            if (!drained(values).equals(expected(values))) {
                ++mismatches;
            }
        }
        assertThat("the heap drains in another order than the standard queue", mismatches, is(0));
    }

    @DisplayName("Вершина кучи — наименьший элемент")
    @Test
    @Timeout(10)
    void keepsTheSmallestValueOnTop() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(5).add(1).add(9).add(3);
        assertThat("the heap top is not the smallest value", heap.top(), is(1));
    }

    @DisplayName("Размер кучи считает добавленные элементы")
    @Test
    @Timeout(10)
    void countsTheAddedValues() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(5).add(1).add(9);
        assertThat("the heap dont count the added values", heap.size(), is(3));
    }

    private static List<Integer> drained(List<Integer> values) {
        Heap<Integer> heap = new Heap.Minimum<>(values.size());
        values.forEach(heap::add);
        List<Integer> drained = new ArrayList<>();
        while (heap.size() > 0) {
            drained.add(heap.deleteRoot());
        }
        return drained;
    }

    private static List<Integer> expected(List<Integer> values) {
        Queue<Integer> queue = new PriorityQueue<>(values);
        List<Integer> ordered = new ArrayList<>();
        while (!queue.isEmpty()) {
            ordered.add(queue.poll());
        }
        return ordered;
    }
}
