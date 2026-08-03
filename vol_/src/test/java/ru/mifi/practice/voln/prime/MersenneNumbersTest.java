package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Числа Мерсенна")
final class MersenneNumbersTest {

    @Test
    @Timeout(1)
    @DisplayName("Формула M_5 = 31")
    void mersenneFormulaForFive() {
        assertThat("mersenne formula doesnt yield 31 for exponent five",
            MersenneNumbers.mersenne(5), is(BigInteger.valueOf(31)));
    }

    @Test
    @Timeout(1)
    @DisplayName("Формула M_2 = 3")
    void mersenneFormulaForTwo() {
        assertThat("mersenne formula doesnt yield 3 for exponent two",
            MersenneNumbers.mersenne(2), is(BigInteger.valueOf(3)));
    }

    @ParameterizedTest
    @Timeout(1)
    @ValueSource(ints = {2, 3, 5, 7, 13, 17, 19})
    @DisplayName("Лукас—Лемер: известные простые показатели")
    void lucasLehmerKnownPrimes(int exponent) {
        assertThat("known prime exponent " + exponent + " doesnt yield mersenne prime",
            MersenneNumbers.isMersennePrime(exponent), is(true));
    }

    @ParameterizedTest
    @Timeout(1)
    @ValueSource(ints = {1, 4, 6, 8, 9, 10, 12})
    @DisplayName("Лукас—Лемер: составные показатели")
    void lucasLehmerCompositeExponents(int exponent) {
        assertThat("composite exponent " + exponent + " wrongly yields mersenne prime",
            MersenneNumbers.isMersennePrime(exponent), is(false));
    }

    @Test
    @Timeout(1)
    @DisplayName("Показатели до 20")
    void exponentsUpTo20() {
        assertThat("mersenne prime exponents up to twenty are wrong",
            MersenneNumbers.mersennePrimeExponentsUpTo(20), is(new int[]{2, 3, 5, 7, 13, 17, 19}));
    }
}
