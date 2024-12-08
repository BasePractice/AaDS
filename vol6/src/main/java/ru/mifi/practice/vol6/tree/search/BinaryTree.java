package ru.mifi.practice.vol6.tree.search;

import ru.mifi.practice.vol6.Counter;

import java.util.Optional;

public interface BinaryTree<T extends Comparable<T>> {

    BinaryTree<T> add(T value);

    void delete(T value);

    Optional<Node<T>> search(T value, Counter counter);

    static <T extends Comparable<T>> Node<T> create(T value) {
        return new Node<>(value);
    }

    final class Node<T extends Comparable<T>> implements Comparable<Node<T>> {
        T value;
        Node<T> left;
        Node<T> right;
        int height;

        private Node(T value) {
            this.value = value;
        }

        @Override
        public int compareTo(Node<T> o) {
            return value.compareTo(o.value);
        }

        @Override
        public String toString() {
            return "(" + (value == null ? "empty" : value) + ")";
        }
    }

    abstract class AbstractBinaryTree<T extends Comparable<T>> implements BinaryTree<T> {
        protected Node<T> root;

        @Override
        public BinaryTree<T> add(T value) {
            root = add(root, value);
            return this;
        }

        protected abstract Node<T> add(Node<T> node, T value);

        @Override
        public void delete(T value) {
            root = delete(root, value);
        }

        protected abstract Node<T> delete(Node<T> node, T value);

        @Override
        public Optional<Node<T>> search(T value, Counter counter) {
            return Optional.ofNullable(search(root, value, counter));
        }

        private Node<T> search(Node<T> node, T value, Counter counter) {
            counter.increment();
            if (node == null || node.value.compareTo(value) == 0) {
                return node;
            }
            if (value.compareTo(node.value) < 0) {
                return search(node.left, value, counter);
            }
            return search(node.right, value, counter);
        }

        private String print(Node<T> node) {
            String result = String.valueOf(node);
            if (node.left != null) {
                result += print(node.left);
            }
            if (node.right != null) {
                result += print(node.right);
            }
            return result;
        }

        @Override
        public String toString() {
            return print(root);
        }
    }

}
