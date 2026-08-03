package ru.mifi.practice.vol4.hash;

import ru.mifi.practice.vol4.Counter;

import static ru.mifi.practice.vol4.hash.Hash.POLY_MOD;
import static ru.mifi.practice.vol4.hash.Hash.POLY_PRIME;

class CachedPolynomial {
    static final int CACHED_POLYNOMIALS = Character.MAX_VALUE;
    final int[] polynomials;
    final int mod;

    CachedPolynomial(int mod, int k) {
        this.mod = mod;
        this.polynomials = new int[CACHED_POLYNOMIALS];
        this.polynomials[0] = 1;
        for (int i = 1; i < this.polynomials.length; i++) {
            this.polynomials[i] = (int) ((long) polynomials[i - 1] * k % mod);
        }
    }

    CachedPolynomial() {
        this(POLY_MOD, POLY_PRIME);
    }

    int hash(int[] hashes, int left, int right) {
        if (hashes.length == 0 || right < left) {
            return 0;
        }
        long result = hashes[right];
        if (left > 0) {
            result -= hashes[left - 1];
        }
        return (int) ((result % mod + mod) % mod);
    }

    int[] hashing(String text, Counter counter) {
        int[] hash = new int[text.length()];
        if (text.isEmpty()) {
            return hash;
        }
        hash[0] = text.charAt(0) % mod;
        counter.increment();
        for (int i = 1; i < text.length(); i++) {
            hash[i] = (int) ((hash[i - 1] + (long) polynomials[i] * text.charAt(i)) % mod);
            counter.increment();
        }
        return hash;
    }
}
