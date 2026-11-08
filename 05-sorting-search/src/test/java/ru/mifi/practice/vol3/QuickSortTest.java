package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@DisplayName("Быстрая сортировка")
final class QuickSortTest {

    @DisplayName("Со случайным опорным упорядочивает элементы")
    @Test
    @Timeout(1)
    void randomlyOrdersElements() {
        assertThat("randomly pivoted quick sort dont order the elements",
            new QuickSort<>(new QuickSort.Strategy.Randomly<Integer>())
                .sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("С разбиением Хоара упорядочивает элементы")
    @Test
    @Timeout(1)
    void halfwayOrdersElements() {
        assertThat("Hoare partitioning drops the pivot out of the recursion",
            new QuickSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }
    static Stream<QuickSort.Strategy<Integer>> strategies() {
        return Stream.of(new QuickSort.Strategy.Halfway<>(), new QuickSort.Strategy.Randomly<>());
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @Timeout(5)
    void sortsBoundaryCases(QuickSort.Strategy<Integer> strategy) {
        List<List<Integer>> cases = List.of(
            List.of(), List.of(1), List.of(1, 2), List.of(2, 1), List.of(1, 1),
            List.of(2, 3, 1), List.of(1, 2, 3, 4, 5), List.of(5, 4, 3, 2, 1),
            List.of(2, 2, 2, 2), List.of(-1, 0, -3, 2, -1),
            List.of(Integer.MAX_VALUE, 0, Integer.MIN_VALUE));
        for (List<Integer> input : cases) {
            assertSorts(strategy, input);
        }
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @Timeout(5)
    void sortsAllShortListsWithDuplicates(QuickSort.Strategy<Integer> strategy) {
        int combinations = 1;
        for (int length = 0; length <= 7; length++) {
            for (int code = 0; code < combinations; code++) {
                List<Integer> input = new ArrayList<>();
                int remaining = code;
                for (int index = 0; index < length; index++) {
                    input.add(remaining % 3 - 1);
                    remaining /= 3;
                }
                assertSorts(strategy, input);
            }
            combinations *= 3;
        }
    }

    private void assertSorts(QuickSort.Strategy<Integer> strategy, List<Integer> input) {
        List<Integer> original = new ArrayList<>(input);
        List<Integer> expected = input.stream().sorted().toList();
        assertEquals(expected, new QuickSort<>(strategy).sort(input, new Counter.Default(), false),
            () -> "Input: " + original);
        assertEquals(original, input, "Sorting must not modify the input");
    }
}
