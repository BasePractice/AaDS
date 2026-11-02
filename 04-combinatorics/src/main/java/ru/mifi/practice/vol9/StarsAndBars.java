package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.commons.ObjectCounter;

import java.math.BigInteger;

/**
 * Метод «звёзд и перегородок»: число решений x1 + … + xk = n в неотрицательных целых равно
 * C(n + k − 1, k − 1).
 */
public final class StarsAndBars {

    private StarsAndBars() {
    }

    public static BigInteger number(int balls, int bins) {
        return number(balls, bins, Counter.create());
    }

    public static BigInteger number(int balls, int bins, Counter counter) {
        return number(balls, bins, counter, ObjectCounter.create());
    }

    public static BigInteger number(int balls, int bins, Counter counter, ObjectCounter objects) {
        if (balls < 0 || bins <= 0) {
            return BigInteger.ZERO;
        }
        return Combinatorics.combinations(balls + bins - 1, bins - 1, counter, objects);
    }
}
