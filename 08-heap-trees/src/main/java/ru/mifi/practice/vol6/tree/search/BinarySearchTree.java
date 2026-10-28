package ru.mifi.practice.vol6.tree.search;

import ru.mifi.practice.commons.Counter;

import java.util.Optional;

/** Несбалансированное двоичное дерево поиска. */
@SuppressWarnings("PMD.OverrideBothEqualsAndHashCodeOnComparable")
public final class BinarySearchTree<T extends Comparable<T>> implements BinaryTree<T> {
    private final Nodes<T> nodes = new Nodes<>();
    Node<T> root;

    @Override
    public BinaryTree<T> add(T value) {
        root = add(root, value);
        return this;
    }

    private Node<T> add(Node<T> node, T value) {
        if (node == null) {
            return new Node<>(value);
        }
        if (value.compareTo(node.value) < 0) {
            node.left = add(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = add(node.right, value);
        }
        return node;
    }

    @Override
    public void delete(T value) {
        root = delete(root, value);
    }

    private Node<T> delete(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        if (value.compareTo(node.value) < 0) {
            node.left = delete(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = delete(node.right, value);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            node.value = minimum(node.right);
            node.right = delete(node.right, node.value);
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

    private T minimum(Node<T> node) {
        T value = node.value;
        while (node.left != null) {
            value = node.left.value;
            node = node.left;
        }
        return value;
    }
}
