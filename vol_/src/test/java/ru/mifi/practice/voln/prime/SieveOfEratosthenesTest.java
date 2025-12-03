package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Решето Эратосфена")
class SieveOfEratosthenesTest {
    @Test
    @DisplayName("Граница меньше 2 — пустой список")
    void borderLessThanTwo() {
        assertArrayEquals(new int[0], SieveOfEratosthenes.primesUpTo(1));
        assertArrayEquals(new int[0], SieveOfEratosthenes.primesUpTo(0));
        assertArrayEquals(new int[0], SieveOfEratosthenes.primesUpTo(-5));
    }

    @Test
    @DisplayName("Простые числа до 30")
    void primesUpTo30() {
        int[] expected = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
        assertArrayEquals(expected, SieveOfEratosthenes.primesUpTo(30));
    }

    @Test
    @DisplayName("Проверка простоты небольших чисел")
    void isPrimeSmall() {
        assertFalse(SieveOfEratosthenes.isPrime(1));
        assertTrue(SieveOfEratosthenes.isPrime(2));
        assertTrue(SieveOfEratosthenes.isPrime(3));
        assertFalse(SieveOfEratosthenes.isPrime(4));
        assertTrue(SieveOfEratosthenes.isPrime(29));
        assertFalse(SieveOfEratosthenes.isPrime(30));
    }
}
