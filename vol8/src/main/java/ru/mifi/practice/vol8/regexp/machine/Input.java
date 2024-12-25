package ru.mifi.practice.vol8.regexp.machine;

import java.util.Optional;

public interface Input {

    static Input of(String text) {
        return new StringInput(text);
    }

    Optional<Object> peek();

    void next();

    Input copy();

    boolean hasNext();

    int index();

    abstract class ObjectsInput implements Input {
        protected final Object[] objects;
        private int index;

        protected ObjectsInput(Object[] objects) {
            this(objects, 0);
        }

        protected ObjectsInput(Object[] objects, int index) {
            this.objects = objects;
            this.index = index;
        }

        @Override
        public Optional<Object> peek() {
            if (index >= objects.length) {
                return Optional.empty();
            }
            return Optional.of(objects[index]);
        }

        @Override
        public void next() {
            if (index < objects.length) {
                index++;
            }
        }

        @Override
        public boolean hasNext() {
            return index < objects.length;
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

    final class StringInput extends ObjectsInput {
        public StringInput(String text) {
            this(text, 0);
        }

        public StringInput(String text, int index) {
            super(text.chars().mapToObj(i -> (char) i).toArray(), index);
        }

        public StringInput(Object[] objects, int index) {
            super(objects, index);
        }

        @Override
        public Input copy() {
            return new StringInput(objects, index());
        }
    }
}
