package ru.mifi.practice.vol1.stack;

/** Абстракция стека LIFO. */
public interface Stack<T> {

    void push(T item);

    T pop();

    T peek();

    boolean isEmpty();
}
