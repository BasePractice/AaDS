package ru.mifi.practice.vol4;

import java.util.Optional;

@FunctionalInterface
public interface Hash {
    int POLY_MOD = Integer.MAX_VALUE;
    int POLY_PRIME = 31;

    int hash(String text);

    final class DefaultHash implements Hash {
        @Override
        public int hash(String text) {
            int hash = 0;
            for (int i = 0; i < text.length(); i++) {
                hash = hash * POLY_PRIME + text.charAt(i);
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
        public int hash(String text) {
            int hash = 0;
            int next = 1;
            for (int i = 0; i < text.length(); i++) {
                hash = (hash + next * text.charAt(i)) % mod;
                next = next * k % mod;
            }
            return hash;
        }
    }

    final class PolynomialHashCached implements Hash, Search {
        private static final int CACHED_POLYNOMIALS = Character.MAX_VALUE;
        private final int[] polynomials;
        private final int mod;

        public PolynomialHashCached(int mod, int k) {
            this.mod = mod;
            this.polynomials = new int[CACHED_POLYNOMIALS];
            this.polynomials[0] = 1;
            for (int i = 1; i < this.polynomials.length; i++) {
                this.polynomials[i] = polynomials[i - 1] * k % mod;
            }
        }

        public PolynomialHashCached() {
            this(POLY_MOD, POLY_PRIME);
        }

        @Override
        public int hash(String text) {
            int[] hashes = hashing(text);
            return hash(hashes, 0, text.length() - 1);
        }

        private int hash(int[] hashes, int left, int right) {
            int result = hashes[right];
            if (left > 0) {
                result -= hashes[left - 1];
            }
            return result % mod;
        }

        private int[] hashing(String text) {
            int[] hash = new int[text.length()];
            hash[0] = text.charAt(0);
            for (int i = 1; i < text.length(); i++) {
                hash[i] = (hash[i - 1] + polynomials[i] * text.charAt(i)) % mod;
            }
            return hash;
        }

        @Override
        public Optional<Index> search(String text, String substring) {
            int[] hashes = hashing(text);
            int subHash = hash(hashing(substring), 0, substring.length() - 1);
            for (int i = 0; i + substring.length() <= hashes.length; i++) {
                if (hash(hashes, i, i + substring.length() - 1) == subHash * polynomials[i]) {
                    return Optional.of(new Index(text, substring, i));
                }
            }
            return Optional.empty();
        }
    }
}
