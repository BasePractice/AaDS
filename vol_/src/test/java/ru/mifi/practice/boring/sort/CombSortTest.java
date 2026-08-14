package ru.mifi.practice.boring.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Сортировка расчёской")
final class CombSortTest {

    @DisplayName("Упорядочивает числа")
    @Test
    @Timeout(1)
    void ordersTheValues() {
        assertThat("comb sort dont order the values",
            new CombSort().sorted(new int[]{5, 3, 9, 1, 3}), is(new int[]{1, 3, 3, 5, 9}));
    }

    @DisplayName("Вытаскивает черепаху из хвоста массива")
    @Test
    @Timeout(1)
    void pullsTheTurtleOutOfTheTail() {
        int[] sorted = new CombSort().sorted(new int[]{2, 3, 4, 5, 6, 7, 8, 9, 1});
        assertThat("the smallest value stuck in the tail", sorted[0], is(1));
    }

    @DisplayName("Не трогает исходный массив")
    @Test
    @Timeout(1)
    void keepsTheSourceIntact() {
        int[] values = {5, 3, 9};
        new CombSort().sorted(values);
        assertThat("comb sort reorders the source array", values, is(new int[]{5, 3, 9}));
    }

    @DisplayName("Массив из одного числа остаётся собой")
    @Test
    @Timeout(1)
    void sortsTheSingleValue() {
        assertThat("the single value array breaks the sorting",
            new CombSort().sorted(new int[]{7}), is(new int[]{7}));
    }
}
