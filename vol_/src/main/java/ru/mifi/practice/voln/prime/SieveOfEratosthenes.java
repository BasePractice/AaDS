package ru.mifi.practice.voln.prime;

import java.util.ArrayList;
import java.util.List;

public final class SieveOfEratosthenes {
    private SieveOfEratosthenes() {
    }

    public static int[] primesUpTo(int n) {
        if (n < 2) {
            return new int[0];
        }
        boolean[] composite = new boolean[n + 1];
        int limit = (int) Math.sqrt(n);
        for (int p = 2; p <= limit; p++) {
            if (!composite[p]) {
                // начинаем вычеркивать с p*p
                for (long k = (long) p * (long) p; k <= n; k += p) {
                    composite[(int) k] = true;
                }
            }
        }
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (!composite[i]) {
                primes.add(i);
            }
        }
        int[] result = new int[primes.size()];
        for (int i = 0; i < primes.size(); i++) {
            result[i] = primes.get(i);
        }
        return result;
    }

    public static boolean isPrime(int value) {
        if (value < 2) {
            return false;
        }
        int limit = (int) Math.sqrt(value);
        int[] primes = primesUpTo(limit);
        for (int p : primes) {
            if (value % p == 0) {
                return value == p;
            }
        }
        return true;
    }
}
