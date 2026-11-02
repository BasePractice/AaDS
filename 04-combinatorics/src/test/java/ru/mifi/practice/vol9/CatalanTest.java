package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Числа Каталана")
final class CatalanTest {

    @DisplayName("Пятое число Каталана равно сорока двум")
    @Test
    @Timeout(1)
    void countsFifthCatalanNumber() {
        assertThat("the fifth Catalan number dont equal 42",
            Catalan.number(5), is(BigInteger.valueOf(42)));
    }

    @DisplayName("Нулевое число Каталана равно единице")
    @Test
    @Timeout(1)
    void countsZeroCatalanNumber() {
        assertThat("the zero Catalan number dont equal one",
            Catalan.number(0), is(BigInteger.ONE));
    }

    @DisplayName("Отрицательный номер отвергается")
    @Test
    @Timeout(1)
    void cannotCountNegativeCatalanNumber() {
        assertThrows(IllegalArgumentException.class, () -> Catalan.number(-1),
            "a negative Catalan number is counted");
    }
}
