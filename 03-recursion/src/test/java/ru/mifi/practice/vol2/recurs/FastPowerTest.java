package ru.mifi.practice.vol2.recurs;

import java.math.BigInteger;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Контрольные примеры к практике по быстрому возведению в степень. */
@Disabled("Учебное задание: слайд 184")
@DisplayName("Быстрое возведение в степень")
@Timeout(2)
final class FastPowerTest {
    @ParameterizedTest
    @CsvSource({"3,13,1594323", "2,10,1024", "-2,3,-8", "-2,4,16", "0,5,0",
        "7,0,1", "7,1,7", "0,0,1", "2,63,9223372036854775808"})
    void findsThePower(long base, int exponent, String expected) {
        BigInteger value = new FastPower().power(BigInteger.valueOf(base), exponent, Counter.create());
        assertThat("the power has the wrong value", value, is(new BigInteger(expected)));
    }

    @Test
    void usesFiveMultiplicationsForExponentThirteen() {
        Counter counter = Counter.create();
        new FastPower().power(BigInteger.valueOf(3), 13, counter);
        assertThat("the number of multiplications is wrong", counter.count(), is(5));
    }

    @Test
    void rejectsNegativeExponent() {
        FastPower power = new FastPower();
        Counter counter = Counter.create();
        assertThrows(IllegalArgumentException.class, () -> power.power(BigInteger.TWO, -1, counter));
    }
}
