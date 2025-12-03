package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Числа Мерсенна")
class MersenneNumbersTest {
    @Test
    @DisplayName("Формула M_p = 2^p - 1")
    void mersenneFormula() {
        assertEquals(BigInteger.valueOf(31), MersenneNumbers.mersenne(5));
        assertEquals(BigInteger.valueOf(3), MersenneNumbers.mersenne(2));
    }

    @Test
    @DisplayName("Лукас—Лемер: известные простые показатели")
    void lucasLehmerKnownPrimes() {
        int[] primes = new int[]{2, 3, 5, 7, 13, 17, 19};
        for (int p : primes) {
            assertTrue(MersenneNumbers.isMersennePrime(p), "M_" + p + " должен быть простым");
        }
    }

    @Test
    @DisplayName("Лукас—Лемер: составные показатели")
    void lucasLehmerCompositeExponents() {
        int[] composite = new int[]{1, 4, 6, 8, 9, 10, 12};
        for (int p : composite) {
            assertFalse(MersenneNumbers.isMersennePrime(p), "M_" + p + " не должен быть простым");
        }
    }

    @Test
    @DisplayName("Показатели до 20")
    void exponentsUpTo20() {
        assertArrayEquals(new int[]{2, 3, 5, 7, 13, 17, 19}, MersenneNumbers.mersennePrimeExponentsUpTo(20));
    }
}
