package ru.mifi.practice.vol6.tree.search;

public final class AVL<T extends Comparable<T>> extends BinaryTree.AbstractBinaryTree<T> {
    private int height(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private int balance(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private void up(Node<T> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private Node<T> lr(Node<T> node) {
        Node<T> next = node.right;
        node.right = next.left;
        next.left = node;
        up(node);
        up(next);
        return next;
    }

    private Node<T> minimum(Node<T> node) {
        Node<T> it = node;
        while (it.left != null) {
            it = it.left;
        }
        return it;
    }

    private Node<T> rr(Node<T> node) {
        Node<T> next = node.left;
        node.left = next.right;
        next.right = node;

        up(node);
        up(next);
        return next;
    }

    @Override
    protected Node<T> add(Node<T> node, T value) {
        if (node == null) {
            return BinaryTree.create(value);
        }
        if (node.value.compareTo(value) > 0) {
            node.left = add(node.left, value);
        } else if (node.value.compareTo(value) < 0) {
            node.right = add(node.right, value);
        } else {
            return node;
        }
        up(node);
        int balance = balance(node);
        //LL
        if (balance > 1 && value.compareTo(node.left.value) < 0) {
            return rr(node);
        }
        //RR
        if (balance < -1 && value.compareTo(node.right.value) > 0) {
            return lr(node);
        }
        //LR
        if (balance > 1 && value.compareTo(node.left.value) > 0) {
            node.left = lr(node.left);
            return rr(node);
        }
        //RL
        if (balance < -1 && value.compareTo(node.right.value) < 0) {
            node.right = rr(node.right);
            return lr(node);
        }
        return node;
    }

    @Override
    protected Node<T> delete(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        if (node.value.compareTo(value) > 0) {
            node.left = delete(node.left, value);
        } else if (node.value.compareTo(value) < 0) {
            node.right = delete(node.right, value);
        } else {
            if (node.left == null || node.right == null) {
                Node<T> temporary;
                if (node.left != null) {
                    temporary = node.left;
                } else {
                    temporary = node.right;
                }

                node = temporary;
            } else {
                Node<T> temporary = minimum(node.right);
                node.value = temporary.value;
                node.right = delete(node.right, temporary.value);
            }
        }
        if (node == null) {
            return null;
        }
        up(node);
        int balance = balance(node);
        //LL
        if (balance > 1 && balance(node.left) >= 0) {
            return rr(node);
        }
        //RR
        if (balance < -1 && balance(node.right) <= 0) {
            return lr(node);
        }
        //LR
        if (balance > 1 && balance(node.left) < 0) {
            node.left = rr(node.left);
            return rr(node);
        }
        //PR
        if (balance < -1 && balance(node.right) > 0) {
            node.right = rr(node.right);
            return lr(node);
        }

        return node;
    }
}
