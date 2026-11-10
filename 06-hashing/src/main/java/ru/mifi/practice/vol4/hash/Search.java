package ru.mifi.practice.vol4.hash;

import ru.mifi.practice.commons.Counter;

import java.util.Optional;

/** Поиск подстроки в тексте по полиномиальному хешу. */
public interface Search {
    Optional<Index> search(String text, String substring, Counter counter);

    /** Сравнивает префиксные хеши и проверяет совпадения по символам. */
    final class PolynomialSearchCached implements Search {
        private final CachedPolynomial poly = new CachedPolynomial();

        @Override
        public Optional<Index> search(String text, String substring, Counter counter) {
            int length = substring.length();
            if (length == 0) {
                return Optional.of(new Index(text, substring, 0));
            }
            if (length > text.length()) {
                return Optional.empty();
            }
            int[] powers = poly.powers(text.length());
            int[] hashes = poly.hashing(text, powers, counter);
            int subHash = poly.hash(poly.hashing(substring, powers, counter), 0, length - 1);
            for (int i = 0; i <= text.length() - length; i++) {
                long shifted = (long) subHash * powers[i] % poly.mod;
                if (poly.hash(hashes, i, i + length - 1) == shifted && text.startsWith(substring, i)) {
                    return Optional.of(new Index(text, substring, i));
                }
            }
            return Optional.empty();
        }
    }

    final class SimpleSearch implements Search {

        @Override
        public Optional<Index> search(String text, String subtext, Counter counter) {
            for (int i = 0; i <= text.length() - subtext.length(); i++) {
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
