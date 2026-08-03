package ru.mifi.practice.vol1.queue;

import ru.mifi.practice.vol1.array.Array;

/** Очередь FIFO поверх массива. */
public final class ArrayQueue<T> implements Queue<T> {
    private final Array<T> array;

    public ArrayQueue(Array<T> array) {
        this.array = array;
    }

    @Override
    public void enqueue(T item) {
        array.addLast(item);
    }

    @Override
    public T dequeue() {
        return array.deleteFirst();
    }

    @Override
    public boolean isEmpty() {
        return array.size() == 0;
    }
}
