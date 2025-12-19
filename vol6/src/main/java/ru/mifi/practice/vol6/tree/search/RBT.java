package ru.mifi.practice.vol6.tree.search;

@SuppressWarnings("PMD.CompareObjectsWithEquals")
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
            int cmp = value.compareTo(current.value);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                current.value = value;
                return root;
            }
        }

        newNode.parent = parent;
        if (parent == null) {
            root = newNode;
        } else if (value.compareTo(parent.value) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        fixInsert(newNode);
        return root;
    }

    private void fixInsert(Node<T> z) {
        Node<T> current = z;
        while (current != root && colorOf(current.parent) == RED) {
            if (current.parent == leftOf(parentOf(current.parent))) {
                Node<T> y = rightOf(parentOf(current.parent));
                if (colorOf(y) == RED) {
                    setColor(current.parent, BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(current.parent), RED);
                    current = parentOf(current.parent);
                } else {
                    if (current == rightOf(current.parent)) {
                        current = current.parent;
                        rotateLeft(current);
                    }
                    setColor(current.parent, BLACK);
                    setColor(parentOf(current.parent), RED);
                    rotateRight(parentOf(current.parent));
                }
            } else {
                Node<T> y = leftOf(parentOf(current.parent));
                if (colorOf(y) == RED) {
                    setColor(current.parent, BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(current.parent), RED);
                    current = parentOf(current.parent);
                } else {
                    if (current == leftOf(current.parent)) {
                        current = current.parent;
                        rotateRight(current);
                    }
                    setColor(current.parent, BLACK);
                    setColor(parentOf(current.parent), RED);
                    rotateLeft(parentOf(current.parent));
                }
            }
        }
        setColor(root, BLACK);
    }

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

    @Override
    protected Node<T> delete(Node<T> node, T value) {
        Node<T> z = findNode(node, value);
        if (z != null) {
            deleteNode(z);
        }
        return root;
    }

    private void deleteNode(Node<T> z) {
        Node<T> x;
        Node<T> px;
        Node<T> y = z;
        int yOriginalColor = colorOf(y);
        if (z.left == null) {
            x = z.right;
            px = z.parent;
            transplant(z, z.right);
        } else if (z.right == null) {
            x = z.left;
            px = z.parent;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = colorOf(y);
            x = y.right;
            if (y.parent == z) {
                px = y;
            } else {
                px = y.parent;
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            setColor(y, colorOf(z));
        }
        if (yOriginalColor == BLACK) {
            fixDelete(x, px);
        }
    }

    private void fixDelete(Node<T> x, Node<T> px) {
        Node<T> currentX = x;
        Node<T> currentPx = px;
        while (currentX != root && colorOf(currentX) == BLACK) {
            if (currentX == leftOf(currentPx)) {
                Node<T> w = rightOf(currentPx);
                if (colorOf(w) == RED) {
                    setColor(w, BLACK);
                    setColor(currentPx, RED);
                    rotateLeft(currentPx);
                    w = rightOf(currentPx);
                }
                if (colorOf(leftOf(w)) == BLACK && colorOf(rightOf(w)) == BLACK) {
                    setColor(w, RED);
                    currentX = currentPx;
                    currentPx = parentOf(currentX);
                } else {
                    if (colorOf(rightOf(w)) == BLACK) {
                        setColor(leftOf(w), BLACK);
                        setColor(w, RED);
                        rotateRight(w);
                        w = rightOf(currentPx);
                    }
                    setColor(w, colorOf(currentPx));
                    setColor(currentPx, BLACK);
                    setColor(rightOf(w), BLACK);
                    rotateLeft(currentPx);
                    currentX = root;
                }
            } else {
                Node<T> w = leftOf(currentPx);
                if (colorOf(w) == RED) {
                    setColor(w, BLACK);
                    setColor(currentPx, RED);
                    rotateRight(currentPx);
                    w = leftOf(currentPx);
                }
                if (colorOf(rightOf(w)) == BLACK && colorOf(leftOf(w)) == BLACK) {
                    setColor(w, RED);
                    currentX = currentPx;
                    currentPx = parentOf(currentX);
                } else {
                    if (colorOf(leftOf(w)) == BLACK) {
                        setColor(rightOf(w), BLACK);
                        setColor(w, RED);
                        rotateLeft(w);
                        w = leftOf(currentPx);
                    }
                    setColor(w, colorOf(currentPx));
                    setColor(currentPx, BLACK);
                    setColor(leftOf(w), BLACK);
                    rotateRight(currentPx);
                    currentX = root;
                }
            }
        }
        setColor(currentX, BLACK);
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
        Node<T> current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private Node<T> findNode(Node<T> node, T value) {
        Node<T> current = node;
        while (current != null) {
            int cmp = value.compareTo(current.value);
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

    private int colorOf(Node<T> node) {
        return node == null ? BLACK : node.custom;
    }

    private void setColor(Node<T> node, int color) {
        if (node != null) {
            node.custom = color;
        }
    }

    private Node<T> parentOf(Node<T> node) {
        return node == null ? null : node.parent;
    }

    private Node<T> leftOf(Node<T> node) {
        return node == null ? null : node.left;
    }

    private Node<T> rightOf(Node<T> node) {
        return node == null ? null : node.right;
    }
}
