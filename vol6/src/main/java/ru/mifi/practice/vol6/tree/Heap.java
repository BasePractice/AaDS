package ru.mifi.practice.vol6.tree;

import java.util.Arrays;

public interface Heap<T extends Comparable<T>> {

    T deleteRoot();

    T top();

    Heap<T> add(T value);

    void refresh();

    void print();

    int size();

    final class Minimum<T extends Comparable<T>> implements Heap<T> {
        public static final String FORMAT = "%5s %5s %5s%n";
        private static final int TOP = 0;
        private final Object[] heap;
        private final int capacity;
        private int size;

        public Minimum(int capacity) {
            this.heap = new Object[capacity + 1];
            this.capacity = capacity;
        }

        private static int positionParent(int position) {
            return position / 2;
        }

        private static int positionLeft(int position) {
            return position * 2;
        }

        private static int positionRight(int position) {
            return position * 2 + 1;
        }

        private static Object nullable(Object value) {
            if (value == null) {
                return "-";
            }
            return value;
        }

        private boolean isLeaf(int position) {
            return position >= size / 2 && position <= size;
        }

        private void swap(int position1, int position2) {
            Object tmp = heap[position1];
            heap[position1] = heap[position2];
            heap[position2] = tmp;
        }

        private void heapify(int position) {
            if (!isLeaf(position)) {
                if (compare(position, positionLeft(position)) > 0
                    || compare(position, positionRight(position)) > 0) {
                    if (compare(positionLeft(position), positionRight(position)) < 0) {
                        swap(position, positionLeft(position));
                        heapify(positionLeft(position));
                    } else {
                        swap(position, positionRight(position));
                        heapify(positionRight(position));
                    }
                }
            }
        }

        @Override
        public Heap<T> add(T value) {
            if (size >= capacity) {
                return this;
            }
            heap[size] = value;
            int current = size;
            ++size;

            while (compare(current, positionParent(current)) < 0) {
                swap(current, positionParent(current));
                current = positionParent(current);
            }
            return this;
        }

        @Override
        public void refresh() {
            for (int position = size / 2; position >= TOP; position--) {
                heapify(position);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deleteRoot() {
            final T pop = (T) heap[TOP];
            heap[TOP] = heap[size];
            --size;
            heapify(TOP);
            return pop;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T top() {
            return (T) heap[TOP];
        }

        @Override
        public void print() {
            System.out.printf(FORMAT, "top", "left", "right");
            for (int k = TOP; k <= size / 2; k++) {
                System.out.printf(FORMAT, nullable(heap[k]), nullable(heap[positionLeft(k) + 1]), nullable(heap[positionRight(k) + 1]));
            }
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
