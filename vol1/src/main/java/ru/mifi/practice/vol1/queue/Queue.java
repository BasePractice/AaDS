package ru.mifi.practice.vol1.queue;

public interface Queue<T> {

    void enqueue(T item);

    T dequeue();

    boolean isEmpty();
}
