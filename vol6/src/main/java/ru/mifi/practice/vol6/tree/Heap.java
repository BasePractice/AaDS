package ru.mifi.practice.vol6.tree;

import java.util.Arrays;
import java.util.NoSuchElementException;

/** Двоичная куча, хранящая минимальный элемент в корне. */
public interface Heap<T extends Comparable<T>> {

    T deleteRoot();

    T top();

    Heap<T> add(T value);

    void refresh();

    void print();

    int size();

    final class Minimum<T extends Comparable<T>> implements Heap<T> {
        private static final String FORMAT = "%5s %5s %5s%n";
        private static final int TOP = 0;
        private final Object[] heap;
        private final int capacity;
        private int size;

        public Minimum(int capacity) {
            this.heap = new Object[capacity];
            this.capacity = capacity;
        }

        private static int positionParent(int position) {
            return (position - 1) / 2;
        }

        private static int positionLeft(int position) {
            return position * 2 + 1;
        }

        private static int positionRight(int position) {
            return position * 2 + 2;
        }

        private static Object nullable(Object value) {
            if (value == null) {
                return "-";
            }
            return value;
        }

        private void swap(int position1, int position2) {
            Object carry = heap[position1];
            heap[position1] = heap[position2];
            heap[position2] = carry;
        }

        private void heapify(int position) {
            int smallest = position;
            int left = positionLeft(position);
            int right = positionRight(position);
            if (left < size && compare(left, smallest) < 0) {
                smallest = left;
            }
            if (right < size && compare(right, smallest) < 0) {
                smallest = right;
            }
            if (smallest != position) {
                swap(position, smallest);
                heapify(smallest);
            }
        }

        @Override
        public Heap<T> add(T value) {
            if (size >= capacity) {
                throw new IllegalStateException("Куча заполнена, вместимость " + capacity);
            }
            heap[size] = value;
            int current = size;
            ++size;
            while (current > TOP && compare(current, positionParent(current)) < 0) {
                swap(current, positionParent(current));
                current = positionParent(current);
            }
            return this;
        }

        @Override
        public void refresh() {
            for (int position = size / 2 - 1; position >= TOP; position--) {
                heapify(position);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deleteRoot() {
            final T pop = (T) heap[requireNotEmpty()];
            --size;
            heap[TOP] = heap[size];
            heap[size] = null;
            heapify(TOP);
            return pop;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T top() {
            return (T) heap[requireNotEmpty()];
        }

        private int requireNotEmpty() {
            if (size == 0) {
                throw new NoSuchElementException("Куча пуста");
            }
            return TOP;
        }

        @Override
        public void print() {
            System.out.printf(FORMAT, "top", "left", "right");
            for (int k = TOP; k < size; k++) {
                System.out.printf(FORMAT, nullable(heap[k]), child(positionLeft(k)), child(positionRight(k)));
            }
        }

        private Object child(int position) {
            if (position >= size) {
                return "-";
            }
            return nullable(heap[position]);
        }

        @Override
        public int size() {
            return size;
        }

        @SuppressWarnings("unchecked")
        private int compare(int position1, int position2) {
            Object left = heap[position1];
            Object right = heap[position2];
            if (left == null) {
                if (right == null) {
                    return 0;
                }
                return 1;
            } else if (right == null) {
                return -1;
            }
            return ((T) left).compareTo((T) right);
        }

        @Override
        public String toString() {
            return Arrays.toString(heap);
        }
    }
}
