package ru.mifi.practice.vol6.tree;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public interface Node<T> extends Visitor.Visit<T> {
    static <T> Node<T> root(T value) {
        return new Default<>(null, value);
    }

    Node<T> parent();

    Node<T> search(T element);

    List<Node<T>> path(T element);

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
        public Node<T> parent() {
            return parent;
        }

        @Override
        public Node<T> search(T element) {
            Queue<Node<T>> queue = new LinkedList<>();
            queue.add(this);
            while (!queue.isEmpty()) {
                Node<T> node = queue.poll();
                if (node.value() != null && node.value().equals(element)) {
                    return node;
                }
                if (node.left() != null) {
                    queue.add(node.left());
                }
                if (node.right() != null) {
                    queue.add(node.right());
                }
            }
            return null;
        }

        @Override
        public List<Node<T>> path(T element) {
            Deque<Node<T>> path = new LinkedList<>();
            Node<T> it = search(element);
            while (it != null) {
                path.push(it);
                if (it == this) {
                    break;
                }
                it = it.parent();
            }
            return new ArrayList<>(path);
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
            if (value == null) {
                return left;
            }
            return left = new Default<>(this, value);
        }

        @Override
        public Node<T> right() {
            return right;
        }

        @Override
        public Node<T> right(T value) {
            if (value == null) {
                return right;
            }
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

        @Override
        public String toString() {
            return "(" + value + ")";
        }
    }
}
