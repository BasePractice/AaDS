package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Поразрядная сортировка")
final class RadixSortTest {

    @DisplayName("Упорядочивает элементы")
    @Test
    @Timeout(1)
    void ordersElements() {
        assertThat("radix sort dont order the elements",
            new RadixSort().sort(List.of(500, 3, 9000, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 500, 9000));
    }

    @DisplayName("Кладёт отрицательные числа левее")
    @Test
    @Timeout(1)
    void keepsNegativesOnTheLeft() {
        assertThat("radix sort dont handle the sign bit",
            new RadixSort().sort(List.of(3, -1, 0, -500, 2), new Counter.Default(), false),
            contains(-500, -1, 0, 2, 3));
    }

    @DisplayName("Справляется с граничными значениями")
    @Test
    @Timeout(1)
    void handlesExtremeValues() {
        assertThat("radix sort dont handle the extremes of int",
            new RadixSort().sort(List.of(Integer.MAX_VALUE, Integer.MIN_VALUE, 0), new Counter.Default(), false),
            contains(Integer.MIN_VALUE, 0, Integer.MAX_VALUE));
    }

    @DisplayName("Справляется с пустым входом")
    @Test
    @Timeout(1)
    void handlesEmptyInput() {
        assertThat("empty input dont produce an empty result",
            new RadixSort().sort(List.of(), new Counter.Default(), false).size(), is(0));
    }
}
