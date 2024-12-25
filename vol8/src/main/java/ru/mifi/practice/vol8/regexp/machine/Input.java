package ru.mifi.practice.vol8.regexp.machine;

import java.util.Optional;

public interface Input {

    static Input of(String text) {
        return new StringInput(text);
    }

    Marker mark();

    default void reset(Marker marker) {
        reset(marker.pos());
    }

    void reset(int pos);

    Optional<Object> peek();

    void next();

    Input copy();

    boolean hasNext();

    int index();

    record Marker(int pos) {
    }

    final class StringInput implements Input {
        private final char[] chars;
        private int index;

        public StringInput(String text) {
            this.chars = text.toCharArray();
        }

        private StringInput(char[] chars, int it) {
            this.chars = chars;
            this.index = it;
        }

        @Override
        public Marker mark() {
            return new Marker(index);
        }

        @Override
        public void reset(int pos) {
            index = pos;
        }

        @Override
        public Optional<Object> peek() {
            if (index >= chars.length) {
                return Optional.empty();
            }
            return Optional.of(chars[index]);
        }

        @Override
        public void next() {
            if (index < chars.length) {
                index++;
            }
        }

        @Override
        public Input copy() {
            return new StringInput(chars, index);
        }

        @Override
        public boolean hasNext() {
            return index < chars.length;
        }

        @Override
        public int index() {
            return index;
        }

        @Override
        public String toString() {
            return index + ":" + peek().orElse(' ');
        }
    }
}
