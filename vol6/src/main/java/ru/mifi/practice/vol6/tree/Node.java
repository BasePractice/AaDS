package ru.mifi.practice.vol6.tree;

import lombok.EqualsAndHashCode;

public interface Node<T> extends Visitor.Visit<T> {
    static <T> Node<T> root(T value) {
        return new Default<>(null, value);
    }

    T value();

    Node<T> left();

    Node<T> left(T value);

    Node<T> right();

    Node<T> right(T value);


    Node<T> deleteLeft(T value);

    Node<T> deleteRight(T value);

    @Override
    void visit(Visitor<T> visitor, VisitorStrategy<T> strategy);

    @EqualsAndHashCode(of = "value")
    final class Default<T> implements Node<T> {
        private final T value;
        private final Node<T> parent;
        private Node<T> left;
        private Node<T> right;

        private Default(Node<T> parent, T value) {
            this.value = value;
            this.parent = parent;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public Node<T> left() {
            return left;
        }

        @Override
        public Node<T> left(T value) {
            return left = new Default<>(this, value);
        }

        @Override
        public Node<T> right() {
            return right;
        }

        @Override
        public Node<T> right(T value) {
            return right = new Default<>(this, value);
        }

        @Override
        public Node<T> deleteLeft(T value) {
            Node<T> node = left;
            left = null;
            return node;
        }

        @Override
        public Node<T> deleteRight(T value) {
            Node<T> node = right;
            right = null;
            return node;
        }

        @Override
        public void visit(Visitor<T> visitor, VisitorStrategy<T> strategy) {
            strategy.visit(this, visitor, strategy);
        }
    }
}
