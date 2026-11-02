package ru.mifi.practice.vol9;

import java.math.BigInteger;

/**
 * Беспорядки: перестановки, в которых ни один элемент не остался на своём месте. Считаются по
 * рекуррентности !n = (n − 1) · (!(n − 1) + !(n − 2)).
 */
public final class Derangements {

    private Derangements() {
    }

    public static BigInteger number(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Размер беспорядка не может быть отрицательным: " + n);
        }
        BigInteger previous = BigInteger.ONE;
        if (n == 0) {
            return previous;
        }
        BigInteger current = BigInteger.ZERO;
        for (int size = 2; size <= n; size++) {
            BigInteger next = BigInteger.valueOf(size - 1L).multiply(previous.add(current));
            previous = current;
            current = next;
        }
        return current;
    }
}
