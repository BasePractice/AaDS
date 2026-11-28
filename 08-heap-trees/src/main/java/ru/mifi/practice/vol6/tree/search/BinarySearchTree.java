package ru.mifi.practice.vol6.tree.search;

import ru.mifi.practice.commons.Counter;

import java.util.Optional;

/** Несбалансированное двоичное дерево поиска. */
@SuppressWarnings("PMD.OverrideBothEqualsAndHashCodeOnComparable")
public final class BinarySearchTree<T extends Comparable<T>> implements BinaryTree<T> {
    private final Nodes<T> nodes = new Nodes<>();
    Node<T> root;

    @Override
    public BinaryTree<T> add(T value, Counter counter) {
        root = add(root, value, counter);
        return this;
    }

    private Node<T> add(Node<T> node, T value, Counter counter) {
        counter.increment();
        if (node == null) {
            return new Node<>(value);
        }
        if (value.compareTo(node.value) < 0) {
            node.left = add(node.left, value, counter);
        } else if (value.compareTo(node.value) > 0) {
            node.right = add(node.right, value, counter);
        }
        return node;
    }

    @Override
    public void delete(T value, Counter counter) {
        root = delete(root, value, counter);
    }

    private Node<T> delete(Node<T> node, T value, Counter counter) {
        counter.increment();
        if (node == null) {
            return null;
        }
        if (value.compareTo(node.value) < 0) {
            node.left = delete(node.left, value, counter);
        } else if (value.compareTo(node.value) > 0) {
            node.right = delete(node.right, value, counter);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            node.value = minimum(node.right, counter);
            node.right = delete(node.right, node.value, counter);
        }
        return node;
    }

    @Override
    public Optional<Node<T>> search(T value, Counter counter) {
        return nodes.search(root, value, counter);
    }

    @Override
    public String toString() {
        return nodes.print(root);
    }

    private T minimum(Node<T> node, Counter counter) {
        counter.increment();
        T value = node.value;
        while (node.left != null) {
            value = node.left.value;
            node = node.left;
            counter.increment();
        }
        return value;
    }
}
