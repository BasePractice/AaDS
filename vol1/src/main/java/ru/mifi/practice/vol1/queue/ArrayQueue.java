package ru.mifi.practice.vol1.queue;

import ru.mifi.practice.vol1.array.Array;
import ru.mifi.practice.vol1.array.StandardArray;

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

    public static void main(String[] args) {
        Queue<Integer> queue = new ArrayQueue<>(new StandardArray<>(100));
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assert !queue.isEmpty();
        assert 1 == queue.dequeue();
        assert 2 == queue.dequeue();
        assert 3 == queue.dequeue();
        assert queue.isEmpty();
    }
}
