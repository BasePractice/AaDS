package ru.mifi.practice.voln.buffer;

import java.util.Arrays;
import java.util.Optional;

public interface CircularBuffer<T> {
    void add(T item);

    Optional<T> take();

    Optional<T> peek();

    boolean isEmpty();

    int size();

    void clear();

    final class Default<T> implements CircularBuffer<T> {
        private final T[] buffer;
        private int head;
        private int tail;
        private int size;

        @SuppressWarnings("unchecked")
        public Default(int capacity) {
            this.buffer = (T[]) new Object[capacity];
            this.head = 0;
            this.tail = 0;
            this.size = 0;
        }

        @Override
        public void add(T item) {
            if (item == null) {
                throw new NullPointerException("Cannot add null to buffer");
            }

            buffer[tail] = item;
            tail = (tail + 1) % buffer.length;

            if (size == buffer.length) {
                head = (head + 1) % buffer.length;
            } else {
                size++;
            }
        }

        @Override
        public Optional<T> take() {
            if (isEmpty()) {
                return Optional.empty();
            }

            final T item = buffer[head];
            buffer[head] = null;
            head = (head + 1) % buffer.length;
            size--;
            return Optional.of(item);
        }

        @Override
        public Optional<T> peek() {
            if (isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(buffer[head]);
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            Arrays.fill(buffer, null);
            head = 0;
            tail = 0;
            size = 0;
        }
    }
}
