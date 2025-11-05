package ru.mifi.practice.vol1.array;

import java.util.Objects;

public final class StandardArray<T> implements Array<T> {
    private int capacity;
    private int size;
    private Object[] array;

    public StandardArray(int capacity) {
        this.size = 0;
        this.capacity = capacity;
        this.array = new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int index) {
        Objects.checkIndex(index, array.length);
        return (T) array[index];
    }

    @Override
    public void set(int index, T value) {
        if (index >= capacity) {
            capacity *= 2;
            Object[] newArray = new Object[capacity];
            System.arraycopy(array, 0, newArray, 0, array.length);
            array = newArray;
        }
        array[index] = value;
        if (index >= size) {
            size = index + 1;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T delete(int index) {
        Objects.checkIndex(index, array.length);
        Objects.checkIndex(index, size);
        if (index < size && index >= 0) {
            T value = (T) array[index];
            if (index != size - 1) {
                //FIXME: Заменить на System.arraycopy
                for (int i = index; i < size - 1; i++) {
                    array[i] = array[i + 1];
                }
                array[size - 1] = null;
            }
            --size;
            return value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }
}
