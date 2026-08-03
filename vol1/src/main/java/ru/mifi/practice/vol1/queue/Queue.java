package ru.mifi.practice.vol1.queue;

/** Абстракция очереди FIFO. */
public interface Queue<T> {

    void enqueue(T item);

    T dequeue();

    boolean isEmpty();
}
