package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.commons.ObjectCounter;

import java.math.BigInteger;

/**
 * Числа Стирлинга. Второго рода S(n, k) — разбиения n элементов на k непустых подмножеств.
 * Первого рода c(n, k) — перестановки n элементов с k циклами.
 */
public final class Stirling {

    private Stirling() {
    }

    public static BigInteger second(int n, int k) {
        return second(n, k, Counter.create());
    }

    public static BigInteger second(int n, int k, Counter counter) {
        return second(n, k, counter, ObjectCounter.create());
    }

    public static BigInteger second(int n, int k, Counter counter, ObjectCounter objects) {
        check(n, k);
        objects.allocate(n + 2);
        BigInteger[][] table = new BigInteger[n + 1][k + 1];
        table[0][0] = BigInteger.ONE;
        for (int row = 1; row <= n; row++) {
            for (int column = 1; column <= Math.min(row, k); column++) {
                counter.increment();
                objects.allocate();
                BigInteger same = BigInteger.valueOf(column).multiply(value(table, row - 1, column));
                BigInteger previous = value(table, row - 1, column - 1);
                table[row][column] = same.add(previous);
            }
        }
        return value(table, n, k);
    }

    public static BigInteger first(int n, int k) {
        return first(n, k, Counter.create());
    }

    public static BigInteger first(int n, int k, Counter counter) {
        return first(n, k, counter, ObjectCounter.create());
    }

    public static BigInteger first(int n, int k, Counter counter, ObjectCounter objects) {
        check(n, k);
        objects.allocate(n + 2);
        BigInteger[][] table = new BigInteger[n + 1][k + 1];
        table[0][0] = BigInteger.ONE;
        for (int row = 1; row <= n; row++) {
            for (int column = 1; column <= Math.min(row, k); column++) {
                counter.increment();
                objects.allocate();
                BigInteger same = BigInteger.valueOf(row - 1L).multiply(value(table, row - 1, column));
                BigInteger previous = value(table, row - 1, column - 1);
                table[row][column] = same.add(previous);
            }
        }
        return value(table, n, k);
    }

    private static BigInteger value(BigInteger[][] table, int row, int column) {
        if (row < 0 || column < 0 || column >= table[row].length) {
            return BigInteger.ZERO;
        }
        BigInteger value = table[row][column];
        return value == null ? BigInteger.ZERO : value;
    }

    private static void check(int n, int k) {
        if (n < 0 || k < 0) {
            throw new IllegalArgumentException("Аргументы чисел Стирлинга не могут быть отрицательными: " + n + ", " + k);
        }
    }
}
