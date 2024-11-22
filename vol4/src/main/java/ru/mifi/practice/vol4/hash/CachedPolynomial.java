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
            this.polynomials[i] = polynomials[i - 1] * k % mod;
        }
    }

    CachedPolynomial() {
        this(POLY_MOD, POLY_PRIME);
    }

    int hash(int[] hashes, int left, int right) {
        int result = hashes[right];
        if (left > 0) {
            result -= hashes[left - 1];
        }
        return result % mod;
    }

    int[] hashing(String text, Counter counter) {
        int[] hash = new int[text.length()];
        hash[0] = text.charAt(0);
        for (int i = 1; i < text.length(); i++) {
            hash[i] = (hash[i - 1] + polynomials[i] * text.charAt(i)) % mod;
            counter.increment();
        }
        return hash;
    }
}
