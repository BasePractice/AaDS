package ru.mifi.practice.vol4.hash;

import ru.mifi.practice.vol4.Counter;

import java.util.Optional;

public interface Search {
    Optional<Index> search(String text, String substring, Counter counter);

    /**
     * Найдите здесь ошибку
     */
    final class PolynomialSearchCached implements Search {
        private final CachedPolynomial poly = new CachedPolynomial();

        @Override
        public Optional<Index> search(String text, String substring, Counter counter) {
            int[] hashes = poly.hashing(text, counter);
            int subHash = poly.hash(poly.hashing(substring, counter), 0, substring.length() - 1);
            for (int i = 0; i + substring.length() <= hashes.length; i++) {
                if (poly.hash(hashes, i, i + substring.length() - 1) == subHash * poly.polynomials[i]) {
                    return Optional.of(new Index(text, substring, i));
                }
            }
            return Optional.empty();
        }
    }

    final class SimpleSearch implements Search {

        @Override
        public Optional<Index> search(String text, String subtext, Counter counter) {
            for (int i = 0; i + subtext.length() < text.length(); i++) {
                int k = i;
                counter.increment();
                for (int j = 0; j < subtext.length(); j++) {
                    counter.increment();
                    if (text.charAt(k) != subtext.charAt(j)) {
                        break;
                    }
                    ++k;
                }
                if (k - i == subtext.length()) {
                    return Optional.of(new Index(text, subtext, i));
                }
                i = k;
            }
            return Optional.empty();
        }
    }

    record Index(String text, String subtext, int index) {

    }
}
