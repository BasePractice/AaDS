package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Треугольник Паскаля")
final class PascalTriangleTest {

    @DisplayName("Пятая строка содержит биномиальные коэффициенты")
    @Test
    @Timeout(1)
    void generatesBinomialCoefficients() {
        assertThat("the fifth row dont contain binomial coefficients",
            PascalTriangle.of(6).get(5),
            contains(BigInteger.ONE, BigInteger.valueOf(5), BigInteger.TEN,
                BigInteger.TEN, BigInteger.valueOf(5), BigInteger.ONE));
    }

    @DisplayName("Первая строка состоит из единицы")
    @Test
    @Timeout(1)
    void startsWithSingleOne() {
        assertThat("the first row dont equal one", PascalTriangle.of(1).get(0), contains(BigInteger.ONE));
    }

    @DisplayName("Ноль строк даёт пустой треугольник")
    @Test
    @Timeout(1)
    void generatesNothingForZeroRows() {
        assertThat("zero rows produce a triangle", PascalTriangle.of(0).size(), is(0));
    }

    @DisplayName("Отрицательное количество строк отвергается")
    @Test
    @Timeout(1)
    void cannotGenerateNegativeRows() {
        assertThrows(IllegalArgumentException.class, () -> PascalTriangle.of(-1),
            "a negative number of rows is accepted");
    }
}
