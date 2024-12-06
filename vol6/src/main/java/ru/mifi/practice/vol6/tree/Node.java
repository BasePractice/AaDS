package ru.mifi.practice.vol6.tree;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public interface Node<T> extends Visitor.Visit<T>, Hashable {
    static <T> Node<T> root(T value) {
        return new Default<>(null, value, new Supplier<>() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Integer get() {
                return counter.incrementAndGet();
            }
        });
    }

    int hash();

    int index();

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
    final class Default<T> implements Node<T>, Comparable<Node<T>> {
        private final T value;
        private final Node<T> parent;
        private final int index;
        private final Supplier<Integer> generator;
        private Node<T> left;
        private Node<T> right;

        private Default(Node<T> parent, T value, Supplier<Integer> generator) {
            this.value = value;
            this.parent = parent;
            this.index = generator.get();
            this.generator = generator;
        }

        @Override
        public int hash() {
            int left = left() != null ? (int) Math.log(left().hash()) : 0;
            int right = right() != null ? (int) Math.log(right().hash()) : 0;
            return Objects.hash(value) + left + right;
        }

        @Override
        public int index() {
            return index;
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
                if (it.equals(this)) {
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
                left = null;
                return null;
            }
            return left = new Default<>(this, value, generator);
        }

        @Override
        public Node<T> right() {
            return right;
        }

        @Override
        public Node<T> right(T value) {
            if (value == null) {
                right = null;
                return null;
            }
            return right = new Default<>(this, value, generator);
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

        @Override
        public int compareTo(Node<T> o) {
            return Integer.compare(index(), o.index());
        }
    }
}
