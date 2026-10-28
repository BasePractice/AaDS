package ru.mifi.practice.vol1.deque;

/** Двусторонняя очередь: добавление и извлечение с обоих концов. */
public interface Deque<T> {

    void addFirst(T item);

    void addLast(T item);

    T removeFirst();

    T removeLast();

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}
