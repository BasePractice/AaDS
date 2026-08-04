package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка сортировок со штатной сортировкой списка на случайных входах.
 *
 * <p>Диапазон значений намеренно узкий, чтобы повторы попадались часто: именно на них ломаются
 * разбиения и неустойчивые слияния. Сортировка со стратегией Хоара сюда не входит — в ней
 * оставлена учебная ошибка, помеченная в JavaDoc класса.
 */
@DisplayName("Сортировки на случайных входах")
final class SortRandomTest {

    @DisplayName("Сортировка слиянием упорядочивает как штатная")
    @Test
    @Timeout(10)
    void mergeSortAgreesWithTheStandardOne() {
        assertThat("merge sort disagrees with the standard sorting",
            disagreements(new MergeSort<>()), is(0));
    }

    @DisplayName("Пирамидальная сортировка упорядочивает как штатная")
    @Test
    @Timeout(10)
    void heapSortAgreesWithTheStandardOne() {
        assertThat("heap sort disagrees with the standard sorting",
            disagreements(new HeapSort<>()), is(0));
    }

    @DisplayName("Поразрядная сортировка упорядочивает как штатная")
    @Test
    @Timeout(10)
    void radixSortAgreesWithTheStandardOne() {
        assertThat("radix sort disagrees with the standard sorting",
            disagreements(new RadixSort()), is(0));
    }

    @DisplayName("Сортировка подсчётом упорядочивает как штатная")
    @Test
    @Timeout(10)
    void countSortAgreesWithTheStandardOne() {
        assertThat("count sort disagrees with the standard sorting",
            disagreements(new CountSort(50)), is(0));
    }

    @DisplayName("Быстрая сортировка со случайной опорой упорядочивает как штатная")
    @Test
    @Timeout(10)
    void randomPivotQuickSortAgreesWithTheStandardOne() {
        assertThat("quick sort with a random pivot disagrees with the standard sorting",
            disagreements(new QuickSort<Integer>(new QuickSort.Strategy.Randomly<>())), is(0));
    }

    @DisplayName("Поразрядная сортировка разводит отрицательные и положительные")
    @Test
    @Timeout(10)
    void radixSortSeparatesNegativesFromPositives() {
        assertThat("radix sort dont put negatives before positives",
            new RadixSort().sort(List.of(3, -1, 2, -7, 0), Counter.create(), false),
            is(List.of(-7, -1, 0, 2, 3)));
    }

    private static int disagreements(Sort<Integer> sort) {
        Random random = new Random(20260804L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            List<Integer> input = new ArrayList<>();
            for (int i = 0; i < random.nextInt(30); i++) {
                input.add(random.nextInt(50));
            }
            List<Integer> expected = new ArrayList<>(input);
            Collections.sort(expected);
            if (!sort.sort(input, Counter.create(), false).equals(expected)) {
                ++mismatches;
            }
        }
        return mismatches;
    }
}
