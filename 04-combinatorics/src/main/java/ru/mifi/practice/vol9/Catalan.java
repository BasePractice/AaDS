package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;

import java.math.BigInteger;

/**
 * Числа Каталана: C(n) = C(2n, n) / (n + 1). Считают правильные скобочные последовательности,
 * двоичные деревья поиска и пути, не поднимающиеся выше диагонали.
 */
public final class Catalan {

    private Catalan() {
    }

    public static BigInteger number(int n) {
        return number(n, Counter.create());
    }

    public static BigInteger number(int n, Counter counter) {
        if (n < 0) {
            throw new IllegalArgumentException("Номер числа Каталана не может быть отрицательным: " + n);
        }
        BigInteger result = Combinatorics.combinations(2 * n, n, counter);
        counter.increment();
        return result.divide(BigInteger.valueOf(n + 1L));
    }
}
