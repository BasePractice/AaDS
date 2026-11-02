package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Числа Белла")
final class BellTest {

    @DisplayName("Пятое число Белла равно пятидесяти двум")
    @Test
    @Timeout(1)
    void countsFifthBellNumber() {
        assertThat("the fifth Bell number dont equal 52",
            Bell.number(5), is(BigInteger.valueOf(52)));
    }

    @DisplayName("Нулевое число Белла равно единице")
    @Test
    @Timeout(1)
    void countsZeroBellNumber() {
        assertThat("the zero Bell number dont equal one", Bell.number(0), is(BigInteger.ONE));
    }

    @DisplayName("Отрицательный номер отвергается")
    @Test
    @Timeout(1)
    void cannotCountNegativeBellNumber() {
        assertThrows(IllegalArgumentException.class, () -> Bell.number(-1),
            "a negative Bell number is counted");
    }
}
