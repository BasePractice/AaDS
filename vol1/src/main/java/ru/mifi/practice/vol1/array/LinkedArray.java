package ru.mifi.practice.vol1.array;

import java.util.Objects;

/** Массив на основе двусвязного списка узлов. */
public final class LinkedArray<T> implements Array<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    @Override
    public T get(int index) {
        return node(Objects.checkIndex(index, size)).value;
    }

    @Override
    public void set(int index, T value) {
        if (index < size) {
            node(index).value = value;
            return;
        }
        while (size <= index) {
            append();
        }
        tail.value = value;
    }

    @Override
    public T delete(int index) {
        Node<T> node = node(Objects.checkIndex(index, size));
        unlink(node);
        --size;
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    private Node<T> node(int index) {
        Node<T> node = head;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node;
    }

    private void append() {
        Node<T> node = new Node<>();
        node.prev = tail;
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        ++size;
    }

    private void unlink(Node<T> node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        node.next = null;
        node.prev = null;
    }

    private static final class Node<T> {
        private T value;
        private Node<T> next;
        private Node<T> prev;
    }
}
