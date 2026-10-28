package ru.mifi.practice.boring.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Битонная сортировка")
final class BitonicSortTest {

    @DisplayName("Упорядочивает числа, когда их степень двойки")
    @Test
    @Timeout(1)
    void ordersThePowerOfTwoLength() {
        assertThat("bitonic sort dont order the values",
            new BitonicSort().sorted(new int[]{5, 3, 9, 1}), is(new int[]{1, 3, 5, 9}));
    }

    @DisplayName("Упорядочивает числа, когда их не степень двойки")
    @Test
    @Timeout(1)
    void ordersTheOddLength() {
        assertThat("the network breaks on a length that is not a power of two",
            new BitonicSort().sorted(new int[]{5, 3, 9, 1, 3}), is(new int[]{1, 3, 3, 5, 9}));
    }

    @DisplayName("Дополнение сети не остаётся в ответе")
    @Test
    @Timeout(1)
    void keepsTheLength() {
        assertThat("the padding of the network leaks into the answer",
            new BitonicSort().sorted(new int[]{5, 3, 9, 1, 3}).length, is(5));
    }

    @DisplayName("Не трогает исходный массив")
    @Test
    @Timeout(1)
    void keepsTheSourceIntact() {
        int[] values = {5, 3, 9};
        new BitonicSort().sorted(values);
        assertThat("bitonic sort reorders the source array", values, is(new int[]{5, 3, 9}));
    }
}
