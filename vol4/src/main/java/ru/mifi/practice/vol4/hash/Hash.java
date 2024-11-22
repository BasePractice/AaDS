package ru.mifi.practice.vol4.hash;

import ru.mifi.practice.vol4.Counter;

@FunctionalInterface
public interface Hash {
    int POLY_MOD = Integer.MAX_VALUE;
    int POLY_PRIME = 31;

    int hash(String text, Counter counter);

    final class DefaultHash implements Hash {
        @Override
        public int hash(String text, Counter counter) {
            int hash = 0;
            for (int i = 0; i < text.length(); i++) {
                hash = hash * POLY_PRIME + text.charAt(i);
                counter.increment();
            }
            return hash;
        }
    }

    final class PolynomialHash implements Hash {
        private final int mod;
        private final int k;

        public PolynomialHash(int mod, int k) {
            this.mod = mod;
            this.k = k;
        }

        public PolynomialHash() {
            this(POLY_MOD, POLY_PRIME);
        }

        @Override
        public int hash(String text, Counter counter) {
            int hash = 0;
            int next = 1;
            for (int i = 0; i < text.length(); i++) {
                hash = (hash + next * text.charAt(i)) % mod;
                next = next * k % mod;
                counter.increment();
            }
            return hash;
        }
    }

    final class PolynomialHashCached implements Hash {
        private final CachedPolynomial poly = new CachedPolynomial();

        @Override
        public int hash(String text, Counter counter) {
            int[] hashes = poly.hashing(text, counter);
            return poly.hash(hashes, 0, text.length() - 1);
        }
    }
}
