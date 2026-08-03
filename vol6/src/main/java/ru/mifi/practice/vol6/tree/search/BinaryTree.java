package ru.mifi.practice.vol6.tree.search;

import ru.mifi.practice.commons.Counter;

import java.util.Optional;

/** Binary search tree of comparable values. */
public interface BinaryTree<T extends Comparable<T>> {

    BinaryTree<T> add(T value);

    void delete(T value);

    Optional<Node<T>> search(T value, Counter counter);

    @SuppressWarnings("PMD.OverrideBothEqualsAndHashCodeOnComparable")
    final class Node<T extends Comparable<T>> implements Comparable<Node<T>> {
        T value;
        Node<T> parent;
        Node<T> left;
        Node<T> right;
        int custom;

        Node(T value) {
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

    /** Чтение дерева поиска: находит значение и печатает узлы в порядке обхода. */
    final class Nodes<T extends Comparable<T>> {
        Optional<Node<T>> search(Node<T> root, T value, Counter counter) {
            return Optional.ofNullable(descend(root, value, counter));
        }

        private Node<T> descend(Node<T> node, T value, Counter counter) {
            counter.increment();
            if (node == null || node.value.compareTo(value) == 0) {
                return node;
            }
            if (value.compareTo(node.value) < 0) {
                return descend(node.left, value, counter);
            }
            return descend(node.right, value, counter);
        }

        String print(Node<T> node) {
            if (node == null) {
                return "";
            }
            String result = String.valueOf(node);
            if (node.left != null) {
                result += print(node.left);
            }
            if (node.right != null) {
                result += print(node.right);
            }
            return result;
        }
    }
}
