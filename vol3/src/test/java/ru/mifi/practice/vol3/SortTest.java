package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Сортировка")
final class SortTest {

    @DisplayName("Сортировка слиянием упорядочивает элементы")
    @Test
    @Timeout(1)
    void mergeSortOrdersElements() {
        assertThat("merge sort dont order the elements",
            new MergeSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Сортировка слиянием не меняет длину")
    @Test
    @Timeout(1)
    void mergeSortKeepsTheLength() {
        assertThat("merge sort changes the number of elements",
            new MergeSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false).size(), is(5));
    }

    @DisplayName("Сортировка слиянием устойчива")
    @Test
    @Timeout(1)
    void mergeSortIsStable() {
        List<Marked> input = List.of(new Marked(1, 'a'), new Marked(1, 'b'), new Marked(0, 'c'));
        assertThat("equal elements swap their original order",
            new MergeSort<Marked>().sort(input, new Counter.Default(), false),
            contains(new Marked(0, 'c'), new Marked(1, 'a'), new Marked(1, 'b')));
    }

    @DisplayName("Сортировка слиянием возвращает новый список")
    @Test
    @Timeout(1)
    void mergeSortDoesNotAliasSingletonInput() {
        List<Integer> input = List.of(1);
        assertThat("single element input is returned by reference",
            new MergeSort<Integer>().sort(input, new Counter.Default(), false) == input, is(false));
    }

    @DisplayName("Сортировка подсчётом упорядочивает элементы")
    @Test
    @Timeout(1)
    void countSortOrdersElements() {
        assertThat("count sort dont order the elements",
            new CountSort(9).sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Сортировка подсчётом не меняет длину")
    @Test
    @Timeout(1)
    void countSortKeepsTheLength() {
        assertThat("count sort changes the number of elements",
            new CountSort(9).sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false).size(), is(5));
    }

    @DisplayName("Сортировка подсчётом на пустом входе даёт пустой результат")
    @Test
    @Timeout(1)
    void countSortHandlesEmptyInput() {
        assertThat("empty input dont produce an empty result",
            new CountSort(9).sort(List.of(), new Counter.Default(), false).size(), is(0));
    }

    @DisplayName("Быстрая сортировка со случайным опорным упорядочивает элементы")
    @Test
    @Timeout(1)
    void randomlyQuickSortOrdersElements() {
        assertThat("randomly pivoted quick sort dont order the elements",
            new QuickSort<>(new QuickSort.Strategy.Randomly<Integer>())
                .sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Пирамидальная сортировка упорядочивает элементы")
    @Test
    @Timeout(1)
    void heapSortOrdersElements() {
        assertThat("heap sort dont order the elements",
            new HeapSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Пирамидальная сортировка не меняет длину")
    @Test
    @Timeout(1)
    void heapSortKeepsTheLength() {
        assertThat("heap sort changes the number of elements",
            new HeapSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false).size(), is(5));
    }

    @DisplayName("Пирамидальная сортировка справляется с пустым входом")
    @Test
    @Timeout(1)
    void heapSortHandlesEmptyInput() {
        assertThat("empty input dont produce an empty result",
            new HeapSort<Integer>().sort(List.of(), new Counter.Default(), false).size(), is(0));
    }

    @DisplayName("Поразрядная сортировка упорядочивает элементы")
    @Test
    @Timeout(1)
    void radixSortOrdersElements() {
        assertThat("radix sort dont order the elements",
            new RadixSort().sort(List.of(500, 3, 9000, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 500, 9000));
    }

    @DisplayName("Поразрядная сортировка кладёт отрицательные числа левее")
    @Test
    @Timeout(1)
    void radixSortKeepsNegativesOnTheLeft() {
        assertThat("radix sort dont handle the sign bit",
            new RadixSort().sort(List.of(3, -1, 0, -500, 2), new Counter.Default(), false),
            contains(-500, -1, 0, 2, 3));
    }

    @DisplayName("Поразрядная сортировка справляется с граничными значениями")
    @Test
    @Timeout(1)
    void radixSortHandlesExtremeValues() {
        assertThat("radix sort dont handle the extremes of int",
            new RadixSort().sort(List.of(Integer.MAX_VALUE, Integer.MIN_VALUE, 0), new Counter.Default(), false),
            contains(Integer.MIN_VALUE, 0, Integer.MAX_VALUE));
    }

    @DisplayName("Поразрядная сортировка справляется с пустым входом")
    @Test
    @Timeout(1)
    void radixSortHandlesEmptyInput() {
        assertThat("empty input dont produce an empty result",
            new RadixSort().sort(List.of(), new Counter.Default(), false).size(), is(0));
    }

    /**
     * Задание к разделу «Сортировка»: в QuickSort есть ошибка, помеченная в JavaDoc класса.
     * Разбиение Хоара возвращает границу, а не итоговую позицию опорного элемента, поэтому
     * рекурсия по [low, middle) и [middle + 1, high) навсегда замораживает элемент middle.
     * Снимите @Disabled и добейтесь зелёного теста.
     */
    @Disabled("Учебное задание: ошибка в QuickSort со стратегией Halfway")
    @DisplayName("Быстрая сортировка с разбиением Хоара упорядочивает элементы")
    @Test
    @Timeout(1)
    void halfwayQuickSortOrdersElements() {
        assertThat("Hoare partitioning drops the pivot out of the recursion",
            new QuickSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    private record Marked(int key, char mark) implements Comparable<Marked> {
        @Override
        public int compareTo(Marked other) {
            return Integer.compare(key, other.key);
        }
    }
}
