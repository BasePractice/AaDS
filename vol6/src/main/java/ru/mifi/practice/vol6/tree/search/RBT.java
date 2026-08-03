package ru.mifi.practice.vol6.tree.search;

/** Self-balancing red-black binary search tree. */
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

    private void fixInsert(Node<T> inserted) {
        Node<T> current = inserted;
        while (current != root && colorOf(current.parent) == RED) {
            if (current.parent == leftOf(parentOf(current.parent))) {
                Node<T> uncle = rightOf(parentOf(current.parent));
                if (colorOf(uncle) == RED) {
                    setColor(current.parent, BLACK);
                    setColor(uncle, BLACK);
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
                Node<T> uncle = leftOf(parentOf(current.parent));
                if (colorOf(uncle) == RED) {
                    setColor(current.parent, BLACK);
                    setColor(uncle, BLACK);
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

    private void rotateLeft(Node<T> node) {
        Node<T> right = node.right;
        node.right = right.left;
        if (right.left != null) {
            right.left.parent = node;
        }
        right.parent = node.parent;
        if (node.parent == null) {
            root = right;
        } else if (node == node.parent.left) {
            node.parent.left = right;
        } else {
            node.parent.right = right;
        }
        right.left = node;
        node.parent = right;
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
        Node<T> target = findNode(node, value);
        if (target != null) {
            deleteNode(target);
        }
        return root;
    }

    private void deleteNode(Node<T> target) {
        Node<T> child;
        Node<T> parent;
        Node<T> successor = target;
        int successorColor = colorOf(successor);
        if (target.left == null) {
            child = target.right;
            parent = target.parent;
            transplant(target, target.right);
        } else if (target.right == null) {
            child = target.left;
            parent = target.parent;
            transplant(target, target.left);
        } else {
            successor = minimum(target.right);
            successorColor = colorOf(successor);
            child = successor.right;
            if (successor.parent == target) {
                parent = successor;
            } else {
                parent = successor.parent;
                transplant(successor, successor.right);
                successor.right = target.right;
                successor.right.parent = successor;
            }
            transplant(target, successor);
            successor.left = target.left;
            successor.left.parent = successor;
            setColor(successor, colorOf(target));
        }
        if (successorColor == BLACK) {
            fixDelete(child, parent);
        }
    }

    private void fixDelete(Node<T> child, Node<T> parent) {
        Node<T> cursor = child;
        Node<T> cursorParent = parent;
        while (cursor != root && colorOf(cursor) == BLACK) {
            if (cursor == leftOf(cursorParent)) {
                Node<T> sibling = rightOf(cursorParent);
                if (colorOf(sibling) == RED) {
                    setColor(sibling, BLACK);
                    setColor(cursorParent, RED);
                    rotateLeft(cursorParent);
                    sibling = rightOf(cursorParent);
                }
                if (colorOf(leftOf(sibling)) == BLACK && colorOf(rightOf(sibling)) == BLACK) {
                    setColor(sibling, RED);
                    cursor = cursorParent;
                    cursorParent = parentOf(cursor);
                } else {
                    if (colorOf(rightOf(sibling)) == BLACK) {
                        setColor(leftOf(sibling), BLACK);
                        setColor(sibling, RED);
                        rotateRight(sibling);
                        sibling = rightOf(cursorParent);
                    }
                    setColor(sibling, colorOf(cursorParent));
                    setColor(cursorParent, BLACK);
                    setColor(rightOf(sibling), BLACK);
                    rotateLeft(cursorParent);
                    cursor = root;
                }
            } else {
                Node<T> sibling = leftOf(cursorParent);
                if (colorOf(sibling) == RED) {
                    setColor(sibling, BLACK);
                    setColor(cursorParent, RED);
                    rotateRight(cursorParent);
                    sibling = leftOf(cursorParent);
                }
                if (colorOf(rightOf(sibling)) == BLACK && colorOf(leftOf(sibling)) == BLACK) {
                    setColor(sibling, RED);
                    cursor = cursorParent;
                    cursorParent = parentOf(cursor);
                } else {
                    if (colorOf(leftOf(sibling)) == BLACK) {
                        setColor(rightOf(sibling), BLACK);
                        setColor(sibling, RED);
                        rotateLeft(sibling);
                        sibling = leftOf(cursorParent);
                    }
                    setColor(sibling, colorOf(cursorParent));
                    setColor(cursorParent, BLACK);
                    setColor(leftOf(sibling), BLACK);
                    rotateRight(cursorParent);
                    cursor = root;
                }
            }
        }
        setColor(cursor, BLACK);
    }

    private void transplant(Node<T> target, Node<T> replacement) {
        if (target.parent == null) {
            root = replacement;
        } else if (target == target.parent.left) {
            target.parent.left = replacement;
        } else {
            target.parent.right = replacement;
        }
        if (replacement != null) {
            replacement.parent = target.parent;
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
