package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Умножение матриц по определению")
final class PlainProductTest {

    @DisplayName("Строка умножается на столбец")
    @Test
    @Timeout(1)
    void multipliesTheRowByTheColumn() {
        int[][] left = {{1, 2}, {3, 4}};
        int[][] right = {{5, 6}, {7, 8}};
        assertThat("the product is wrong", new PlainProduct().product(left, right),
            is(new int[][]{{19, 22}, {43, 50}}));
    }

    @DisplayName("Порядок сомножителей важен")
    @Test
    @Timeout(1)
    void keepsTheOrderOfTheFactors() {
        int[][] left = {{1, 2}, {3, 4}};
        int[][] right = {{5, 6}, {7, 8}};
        assertThat("the product suddenly commutes", new PlainProduct().product(right, left),
            is(new int[][]{{23, 34}, {31, 46}}));
    }

    @DisplayName("Нулевая матрица обнуляет произведение")
    @Test
    @Timeout(1)
    void multipliesByZeroes() {
        assertThat("the product with zeroes is not zero",
            new PlainProduct().product(new int[][]{{1, 2}, {3, 4}}, new int[2][2]), is(new int[2][2]));
    }
}
