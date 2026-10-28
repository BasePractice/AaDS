package ru.mifi.practice.boring.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Сортировка Шелла")
final class ShellSortTest {

    @DisplayName("Упорядочивает числа")
    @Test
    @Timeout(1)
    void ordersTheValues() {
        assertThat("shell sort dont order the values",
            new ShellSort().sorted(new int[]{5, 3, 9, 1, 3}), is(new int[]{1, 3, 3, 5, 9}));
    }

    @DisplayName("Переставляет далёкие элементы, не сдвигая соседей по одному")
    @Test
    @Timeout(1)
    void movesTheDistantValues() {
        int[] sorted = new ShellSort().sorted(new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
        assertThat("the value from the tail dont reach the head", sorted[0], is(0));
    }

    @DisplayName("Не трогает исходный массив")
    @Test
    @Timeout(1)
    void keepsTheSourceIntact() {
        int[] values = {5, 3, 9};
        new ShellSort().sorted(values);
        assertThat("shell sort reorders the source array", values, is(new int[]{5, 3, 9}));
    }

    @DisplayName("Пустой массив остаётся пустым")
    @Test
    @Timeout(1)
    void sortsTheEmptyArray() {
        assertThat("the empty array breaks the sorting",
            new ShellSort().sorted(new int[0]).length, is(0));
    }
}
