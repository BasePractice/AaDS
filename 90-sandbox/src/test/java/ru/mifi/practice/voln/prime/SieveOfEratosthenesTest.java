package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Решето Эратосфена")
final class SieveOfEratosthenesTest {

    @ParameterizedTest
    @Timeout(1)
    @ValueSource(ints = {1, 0, -5})
    @DisplayName("Граница меньше 2 — пустой список")
    void borderLessThanTwo(int bound) {
        assertThat("bound " + bound + " doesnt yield empty prime list",
            SieveOfEratosthenes.primesUpTo(bound), is(new int[0]));
    }

    @Test
    @Timeout(1)
    @DisplayName("Простые числа до 30")
    void primesUpTo30() {
        assertThat("primes up to thirty are wrong",
            SieveOfEratosthenes.primesUpTo(30), is(new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29}));
    }

    @ParameterizedTest
    @Timeout(1)
    @CsvSource({"1,false", "2,true", "3,true", "4,false", "29,true", "30,false"})
    @DisplayName("Проверка простоты небольших чисел")
    void isPrimeSmall(int number, boolean expected) {
        assertThat("primality verdict for " + number + " is wrong",
            SieveOfEratosthenes.isPrime(number), is(expected));
    }
}
