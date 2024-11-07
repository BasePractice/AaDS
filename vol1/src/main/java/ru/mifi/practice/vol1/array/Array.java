package ru.mifi.practice.vol1.array;

public interface Array<T> {
    T get(int index);

    default void addLast(T element) {
        set(size(), element);
    }

    void set(int index, T value);

    T delete(int index);

    default T deleteLast() {
        return delete(size() - 1);
    }

    default void push(T element) {
        addLast(element);
    }

    int size();

}
