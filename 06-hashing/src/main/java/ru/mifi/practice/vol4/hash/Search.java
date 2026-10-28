package ru.mifi.practice.vol4.hash;

import ru.mifi.practice.commons.Counter;

import java.util.Optional;

/** Поиск подстроки в тексте по полиномиальному хешу. */
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
            for (int i = 0; i + subtext.length() <= text.length(); i++) {
                int cursor = i;
                counter.increment();
                for (int j = 0; j < subtext.length(); j++) {
                    counter.increment();
                    if (text.charAt(cursor) != subtext.charAt(j)) {
                        break;
                    }
                    ++cursor;
                }
                if (cursor - i == subtext.length()) {
                    return Optional.of(new Index(text, subtext, i));
                }
            }
            return Optional.empty();
        }
    }

    record Index(String text, String subtext, int index) {

    }
}
