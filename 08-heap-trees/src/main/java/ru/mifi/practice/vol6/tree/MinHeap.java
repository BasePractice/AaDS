package ru.mifi.practice.vol6.tree;

import java.util.Comparator;
import java.util.List;

/**
 * Заготовка к заданию слайда 437: куча на массиве с операциями add, poll, peek, size. Собирается
 * из готового списка за O(n) — просеиванием вниз от середины к корню, а не n вставками за
 * O(n log n). Порядок задаёт Comparator, поэтому max-heap получается без правки алгоритма.
 * Образец решения — Heap.Minimum с методом refresh.
 */
public interface MinHeap<T> {

    void add(T value);

    T poll();

    T peek();

    int size();

    /**
     * Место для решения студента.
     */
    final class Array<T> implements MinHeap<T> {

        public Array(List<T> items, Comparator<? super T> order) {
            throw new UnsupportedOperationException("Задание слайда 437 ещё не выполнено");
        }

        @Override
        public void add(T value) {
            throw new UnsupportedOperationException("Задание слайда 437 ещё не выполнено");
        }

        @Override
        public T poll() {
            throw new UnsupportedOperationException("Задание слайда 437 ещё не выполнено");
        }

        @Override
        public T peek() {
            throw new UnsupportedOperationException("Задание слайда 437 ещё не выполнено");
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Задание слайда 437 ещё не выполнено");
        }
    }
}
