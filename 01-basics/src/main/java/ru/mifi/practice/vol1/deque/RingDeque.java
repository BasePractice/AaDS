package ru.mifi.practice.vol1.deque;

import java.util.NoSuchElementException;

/**
 * Дек на кольцевом массиве. Голова может уходить влево за ноль и появляться в конце массива,
 * поэтому оба конца растут за O(1) без сдвига элементов. Когда места не остаётся, массив
 * удваивается и элементы переписываются по порядку с нуля — амортизированно та же O(1).
 */
public final class RingDeque<T> implements Deque<T> {
    private Object[] items;
    private int head;
    private int size;

    public RingDeque(int capacity) {
        this.items = new Object[Math.max(1, capacity)];
    }

    @Override
    public void addFirst(T item) {
        grow();
        head = (head - 1 + items.length) % items.length;
        items[head] = item;
        ++size;
    }

    @Override
    public void addLast(T item) {
        grow();
        items[(head + size) % items.length] = item;
        ++size;
    }

    @Override
    public T removeFirst() {
        T item = take(requireNotEmpty(head));
        head = (head + 1) % items.length;
        --size;
        return item;
    }

    @Override
    public T removeLast() {
        T item = take(requireNotEmpty((head + size - 1) % items.length));
        --size;
        return item;
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private T take(int position) {
        T item = (T) items[position];
        items[position] = null;
        return item;
    }

    private int requireNotEmpty(int position) {
        if (size == 0) {
            throw new NoSuchElementException("Дек пуст");
        }
        return position;
    }

    private void grow() {
        if (size < items.length) {
            return;
        }
        Object[] larger = new Object[items.length * 2];
        for (int i = 0; i < size; i++) {
            larger[i] = items[(head + i) % items.length];
        }
        items = larger;
        head = 0;
    }
}
