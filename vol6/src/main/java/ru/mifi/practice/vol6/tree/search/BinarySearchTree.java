package ru.mifi.practice.vol6.tree.search;

@SuppressWarnings("PMD.OverrideBothEqualsAndHashCodeOnComparable")
public final class BinarySearchTree<T extends Comparable<T>> extends BinaryTree.AbstractBinaryTree<T> {


    @Override
    protected Node<T> delete(Node<T> node, T value) {
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

    private T minimum(Node<T> node) {
        T value = node.value;
        while (node.left != null) {
            value = node.left.value;
            node = node.left;
        }
        return value;
    }

    @Override
    protected Node<T> add(Node<T> node, T value) {
        if (node == null) {
            return BinaryTree.create(value);
        }

        if (value.compareTo(node.value) < 0) {
            node.left = add(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = add(node.right, value);
        }
        return node;
    }


}
