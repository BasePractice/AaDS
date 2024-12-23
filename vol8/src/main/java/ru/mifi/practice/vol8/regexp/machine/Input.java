package ru.mifi.practice.vol8.regexp.machine;

import java.util.Optional;

public interface Input {

    static Input of(String text) {
        return new StringInput(text);
    }

    Marker mark();

    void reset(Marker marker);

    Optional<Character> peek();

    void next();

    Input copy();

    boolean hasNext();

    record Marker(int pos) {
    }

    final class StringInput implements Input {
        private final char[] chars;
        private int it;

        public StringInput(String text) {
            this.chars = text.toCharArray();
        }

        private StringInput(char[] chars, int it) {
            this.chars = chars;
            this.it = it;
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

        @Override
        public Input copy() {
            return new StringInput(chars, it);
        }

        @Override
        public boolean hasNext() {
            return it < chars.length;
        }
    }
}
