package ru.mifi.practice.vol1.array;

public final class LinkedArray<T> implements Array<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    @Override
    public T get(int index) {
        for (Node<T> node = head; node != null; node = node.next) {
            if (index == 0) {
                return node.value;
            }
            --index;
        }
        return null;
    }

    @Override
    public void set(int index, T value) {
        if (index >= size) {
            for (int i = index - size; i <= size; ++i) {
                Node<T> node = new Node<>();
                if (head == null) {
                    head = node;
                    tail = node;
                    tail.next = node;
                    node.prev = tail;
                    continue;
                }
                node.prev = tail;
                tail.next = node;
                tail = node;
            }
            tail.value = value;
            size = index;
        } else {
            for (Node<T> node = head; node != null; node = node.next) {
                if (index == 0) {
                    node.value = value;
                    return;
                }
                --index;
            }
        }
    }

    @Override
    public T delete(int index) {
        //FIXME: Реализовать удаление произвольного элемента списка
        return null;
    }

    @Override
    public int size() {
        return size + 1;
    }

    @SuppressWarnings("unused")
    private static final class Node<T> {
        private T value;
        private Node<T> next;
        private Node<T> prev;
    }
}
