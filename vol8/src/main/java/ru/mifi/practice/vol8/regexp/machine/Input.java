package ru.mifi.practice.vol8.regexp.machine;

import java.util.Optional;

public interface Input {

    Marker mark();

    void reset(Marker marker);

    Optional<Character> peek();

    void next();

    record Marker(int pos) {
    }

    final class StringInput implements Input {
        private final char[] chars;
        private int it;

        public StringInput(String text) {
            this.chars = text.toCharArray();
        }

        @Override
        public Marker mark() {
            return new Marker(it);
        }

        @Override
        public void reset(Marker marker) {
            it = marker.pos();
        }

        @Override
        public Optional<Character> peek() {
            if (it >= chars.length) {
                return Optional.empty();
            }
            return Optional.of(chars[it]);
        }

        @Override
        public void next() {
            if (it < chars.length) {
                it++;
            }
        }
    }
}
