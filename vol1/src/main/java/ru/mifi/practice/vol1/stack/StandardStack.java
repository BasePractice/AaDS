package ru.mifi.practice.vol1.stack;

import ru.mifi.practice.vol1.array.Array;

public final class StandardStack<T> implements Stack<T> {
    private final Array<T> array;

    public StandardStack(Array<T> array) {
        this.array = array;
    }

    @Override
    public void push(T item) {
        array.push(item);
    }

    @Override
    public T pop() {
        return array.deleteLast();
    }

    @Override
    public T peek() {
        return array.get(array.size() - 1);
    }

    @Override
    public boolean isEmpty() {
        return array.size() == 0;
    }
}
