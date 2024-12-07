package ru.mifi.practice.vol6.tree;

import java.util.Objects;
import java.util.Optional;

public interface Node2<T> {
    int DEFAULT_CHILDREN = 3;
    int LEFT = 0;
    int RIGHT = 1;

    static <T> Node2<T> root(T value) {
        return root(value, DEFAULT_CHILDREN);
    }

    static <T> Node2<T> root(T value, int capacity) {
        return new DefaultNode<>(null, value);
    }

    Optional<Node2<T>> parent();

    Optional<T> value();

    Node2<T>[] children();

    default int level() {
        Optional<Node2<T>> parent = parent();
        int level = 0;
        while (parent.isPresent()) {
            level++;
            parent = parent.get().parent();
        }
        return level;
    }

    default Optional<Node2<T>> left() {
        return Optional.ofNullable(children()[LEFT]);
    }

    default Optional<Node2<T>> right() {
        return Optional.ofNullable(children()[RIGHT]);
    }

    //
    void setValue(T value);

    Node2<T> setChildren(int index, T value);

    default Node2<T> setLeft(T value) {
        return setChildren(LEFT, value);
    }

    default Node2<T> setRight(T value) {
        return setChildren(RIGHT, value);
    }

    final class DefaultNode<T> implements Node2<T> {
        private final Node2<T> parent;
        private final Object[] children;
        private final int level;
        private T value;

        private DefaultNode(Node2<T> parent, T value, int capacity) {
            this.parent = parent;
            this.value = value;
            this.level = Optional.ofNullable(parent).map(Node2::level).orElse(0);
            this.children = new Object[capacity];
        }

        private DefaultNode(Node2<T> parent, T value) {
            this(parent, value, DEFAULT_CHILDREN);
        }

        @Override
        public int level() {
            return level;
        }

        @Override
        public void setValue(T value) {
            this.value = value;
        }

        @Override
        public Node2<T> setChildren(int index, T value) {
            Objects.checkIndex(index, children.length);
            DefaultNode<T> node = new DefaultNode<>(this, value, children.length);
            children[index] = node;
            return node;
        }

        @Override
        public Optional<Node2<T>> parent() {
            return Optional.ofNullable(parent);
        }

        @Override
        public Optional<T> value() {
            return Optional.ofNullable(value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node2<T>[] children() {
            return (Node2<T>[]) children;
        }
    }
}
