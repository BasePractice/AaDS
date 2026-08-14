package ru.mifi.practice.boring.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Поразрядная сортировка")
final class RadixSortTest {

    @DisplayName("Упорядочивает числа")
    @Test
    @Timeout(1)
    void ordersTheValues() {
        assertThat("radix sort dont order the values",
            new RadixSort().sorted(new int[]{531, 97, 3, 1024, 97}), is(new int[]{3, 97, 97, 531, 1024}));
    }

    @DisplayName("Ставит отрицательные числа перед положительными")
    @Test
    @Timeout(1)
    void putsNegativesBeforePositives() {
        assertThat("radix sort dont separate negatives from positives",
            new RadixSort().sorted(new int[]{3, -1, 2, -7, 0}), is(new int[]{-7, -1, 0, 2, 3}));
    }

    @DisplayName("Держит края диапазона на своих местах")
    @Test
    @Timeout(1)
    void keepsTheEdgesOfTheRangeInPlace() {
        assertThat("the edges of the int range end up in the wrong order",
            new RadixSort().sorted(new int[]{Integer.MAX_VALUE, 0, Integer.MIN_VALUE}),
            is(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}));
    }

    @DisplayName("Не трогает исходный массив")
    @Test
    @Timeout(1)
    void keepsTheSourceIntact() {
        int[] values = {5, 3, 9};
        new RadixSort().sorted(values);
        assertThat("radix sort reorders the source array", values, is(new int[]{5, 3, 9}));
    }
}
