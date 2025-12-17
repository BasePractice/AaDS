package ru.mifi.practice.vol6.tree.search;

public final class RBT<T extends Comparable<T>> extends BinaryTree.AbstractBinaryTree<T> {
    private static final int RED = 0;
    private static final int BLACK = 1;

    @Override
    protected Node<T> add(Node<T> node, T value) {
        Node<T> newNode = BinaryTree.create(value);
        newNode.custom = RED;
        Node<T> parent = null;
        Node<T> current = root;

        while (current != null) {
            parent = current;
            int cmp = newNode.compareTo(current);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                current.value = value;
                return current;
            }
        }
        newNode.parent = parent;
        if (parent == null) {
            root = newNode;
        } else if (newNode.compareTo(parent) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        fixInsert(newNode);
        return newNode;
    }

    private void fixInsert(Node<T> node) {
        while (node != root && node.parent != null && node.parent.custom == RED) {
            if (node.parent.parent != null && node.parent == node.parent.parent.left) {
                Node<T> uncle = node.parent.parent.right;
                // Случай 1: дядя красный
                if (uncle != null && uncle.custom == RED) {
                    node.parent.custom = BLACK;
                    uncle.custom = BLACK;
                    node.parent.parent.custom = RED;
                    node = node.parent.parent;
                } else {
                    // Случай 2: узел является правым потомком
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    // Случай 3: узел является левым потомком
                    node.parent.custom = BLACK;
                    node.parent.parent.custom = RED;
                    rotateRight(node.parent.parent);
                }
            } else if (node.parent.parent != null) {
                // Симметричный случай
                Node<T> uncle = node.parent.parent.left;

                if (uncle != null && uncle.custom == RED) {
                    node.parent.custom = BLACK;
                    uncle.custom = BLACK;
                    node.parent.parent.custom = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.custom = BLACK;
                    node.parent.parent.custom = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.custom = BLACK;
    }

    // Вращения
    private void rotateLeft(Node<T> x) {
        Node<T> right = x.right;
        x.right = right.left;

        if (right.left != null) {
            right.left.parent = x;
        }

        right.parent = x.parent;

        if (x.parent == null) {
            root = right;
        } else if (x == x.parent.left) {
            x.parent.left = right;
        } else {
            x.parent.right = right;
        }

        right.left = x;
        x.parent = right;
    }

    private void rotateRight(Node<T> node) {
        Node<T> left = node.left;
        node.left = left.right;

        if (left.right != null) {
            left.right.parent = node;
        }

        left.parent = node.parent;

        if (node.parent == null) {
            root = left;
        } else if (node == node.parent.left) {
            node.parent.left = left;
        } else {
            node.parent.right = left;
        }

        left.right = node;
        node.parent = left;
    }

    private void deleteNode(Node<T> z) {
        Node<T> y = z;
        Node<T> x;
        int yOriginalColor = y.custom;

        if (z.left == null) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == null) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.custom;
            x = y.right;

            if (y.parent == z) {
                if (x != null) x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }

            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.custom = z.custom;
        }

        if (yOriginalColor == BLACK && x != null) {
            fixDelete(x);
        }
    }

    private void transplant(Node<T> u, Node<T> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }

        if (v != null) {
            v.parent = u.parent;
        }
    }

    private Node<T> minimum(Node<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private void fixDelete(Node<T> x) {
        while (x != root && x.custom == BLACK) {
            if (x == x.parent.left) {
                Node<T> w = x.parent.right;

                if (w.custom == RED) {
                    w.custom = BLACK;
                    x.parent.custom = RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }

                if ((w.left == null || w.left.custom == BLACK) &&
                    (w.right == null || w.right.custom == BLACK)) {
                    w.custom = RED;
                    x = x.parent;
                } else {
                    if (w.right == null || w.right.custom == BLACK) {
                        w.left.custom = BLACK;
                        w.custom = RED;
                        rotateRight(w);
                        w = x.parent.right;
                    }

                    w.custom = x.parent.custom;
                    x.parent.custom = BLACK;
                    if (w.right != null) w.right.custom = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else {
                // Симметричный случай
                Node<T> w = x.parent.left;

                if (w.custom == RED) {
                    w.custom = BLACK;
                    x.parent.custom = RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }

                if ((w.right == null || w.right.custom == BLACK) &&
                    (w.left == null || w.left.custom == BLACK)) {
                    w.custom = RED;
                    x = x.parent;
                } else {
                    if (w.left == null || w.left.custom == BLACK) {
                        w.right.custom = BLACK;
                        w.custom = RED;
                        rotateLeft(w);
                        w = x.parent.left;
                    }

                    w.custom = x.parent.custom;
                    x.parent.custom = BLACK;
                    if (w.left != null) w.left.custom = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }

        if (x != null) {
            x.custom = BLACK;
        }
    }

    @Override
    protected Node<T> delete(Node<T> node, T value) {
        Node<T> deleting = findNode(node, value);
        if (deleting != null) {
            deleteNode(deleting);
        }
        return deleting;
    }

    private Node<T> findNode(Node<T> root, T value) {
        Node<T> current = root;
        while (current != null) {
            int cmp = current.value.compareTo(value);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }
}
