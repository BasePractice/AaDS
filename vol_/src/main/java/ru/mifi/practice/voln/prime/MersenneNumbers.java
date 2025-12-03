package ru.mifi.practice.voln.prime;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Алгоритмы для чисел Мерсенна: M_p = 2^p - 1.
 */
public final class MersenneNumbers {
    private MersenneNumbers() {
    }

    /**
     * Возвращает M_p = 2^p - 1.
     *
     * @param p показатель
     * @return число Мерсенна для показателя p
     */
    public static BigInteger mersenne(int p) {
        if (p < 0) {
            throw new IllegalArgumentException("p должен быть неотрицательным");
        }
        return BigInteger.ONE.shiftLeft(p).subtract(BigInteger.ONE);
    }

    /**
     * Проверка простоты числа Мерсенна методом Лукаса—Лемера.
     * Работает только для p — простого. Для составного p возвращает false.
     * Специальный случай: p = 2 (M_2 = 3) считается простым.
     *
     * @param p показатель
     * @return true если M_p — простое
     */
    public static boolean isMersennePrime(int p) {
        if (p == 2) {
            return true;
        }
        if (p < 2 || !SieveOfEratosthenes.isPrime(p)) {
            return false;
        }
        BigInteger m = mersenne(p);
        BigInteger s = BigInteger.valueOf(4);
        for (int i = 0; i < p - 2; i++) {
            s = s.multiply(s).subtract(BigInteger.TWO).mod(m);
        }
        return s.signum() == 0;
    }

    /**
     * Возвращает список показателей p ≤ maxP, для которых M_p — простое число.
     *
     * @param maxP верхняя граница показателя p (включительно)
     * @return массив показателей p
     */
    public static int[] mersennePrimeExponentsUpTo(int maxP) {
        if (maxP < 2) {
            return new int[0];
        }
        List<Integer> exps = new ArrayList<>();
        for (int p = 2; p <= maxP; p++) {
            if (isMersennePrime(p)) {
                exps.add(p);
            }
        }
        int[] result = new int[exps.size()];
        for (int i = 0; i < exps.size(); i++) {
            result[i] = exps.get(i);
        }
        return result;
    }
}
