package ru.mifi.practice.voln.codes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка сложения по модулю два над массивами байтов — основы кодов над GF(2). */
@DisplayName("Сложение байтов по модулю два")
final class BytesXorTest {

    @DisplayName("Нулевой вектор состоит из нулей")
    @Test
    @Timeout(1)
    void fillsTheZeroVectorWithZeros() {
        assertThat("the zero vector holds something else", BytesXor.zeros(3), is(new byte[]{0, 0, 0}));
    }

    @DisplayName("Прибавление нуля ничего не меняет")
    @Test
    @Timeout(1)
    void keepsTheAccumulatorWhenAddingZeros() {
        byte[] accumulator = {1, 2, 3};
        BytesXor.xorInPlace(accumulator, BytesXor.zeros(3));
        assertThat("adding zeros changes the accumulator", accumulator, is(new byte[]{1, 2, 3}));
    }

    /** Своя противоположность: над GF(2) прибавление вектора к себе обнуляет его. */
    @DisplayName("Прибавление самого себя обнуляет накопитель")
    @Test
    @Timeout(1)
    void zeroesTheAccumulatorWhenAddingItself() {
        byte[] accumulator = {5, 7, 9};
        BytesXor.xorInPlace(accumulator, new byte[]{5, 7, 9});
        assertThat("adding itself dont zero the accumulator", accumulator, is(new byte[]{0, 0, 0}));
    }

    @DisplayName("Двойное прибавление возвращает исходное")
    @Test
    @Timeout(1)
    void returnsTheOriginalOnASecondAdding() {
        byte[] accumulator = {1, 2, 3};
        BytesXor.xorInPlace(accumulator, new byte[]{9, 8, 7});
        BytesXor.xorInPlace(accumulator, new byte[]{9, 8, 7});
        assertThat("a second adding dont return the original", accumulator, is(new byte[]{1, 2, 3}));
    }

    @DisplayName("Массивы разной длины отвергаются")
    @Test
    @Timeout(1)
    void refusesArraysOfDifferentLength() {
        assertThrows(IllegalArgumentException.class,
            () -> BytesXor.xorInPlace(new byte[]{1, 2}, new byte[]{1}),
            "arrays of different length pass the check");
    }
}
