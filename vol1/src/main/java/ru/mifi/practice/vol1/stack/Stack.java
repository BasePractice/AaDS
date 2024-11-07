package ru.mifi.practice.vol1.stack;

public interface Stack<T> {

    void push(T item);

    T pop();

    T peek();

    boolean isEmpty();
}
