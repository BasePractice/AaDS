package ru.mifi.practice.vol6.tree.search;

import java.util.ArrayList;
import java.util.List;

/** Учебное B-дерево целых ключей минимальной степени 2. */
public final class BTree {
    private static final int MAX_KEYS = 3;
    private static final int MEDIAN = 1;

    private Node root = new Node();

    public Search search(int key) {
        Node node = root;
        int visited = 0;
        while (!node.keys.isEmpty()) {
            visited++;
            int index = node.position(key);
            if (node.matches(index, key)) {
                return new Search(true, visited);
            }
            if (node.isLeaf()) {
                break;
            }
            node = node.children.get(index);
        }
        return new Search(false, visited);
    }

    /** Возвращает false для дубликата и сохраняет исходную форму дерева. */
    public boolean add(int key) {
        if (search(key).found()) {
            return false;
        }
        if (root.isFull()) {
            Node newRoot = new Node();
            newRoot.children.add(root);
            newRoot.splitChild(0);
            root = newRoot;
        }
        root.insert(key);
        return true;
    }

    /** Снимок структуры для сравнения с рисунками и проверки инвариантов. */
    public Shape snapshot() {
        return root.snapshot();
    }

    public record Search(boolean found, int visitedNodes) {
    }

    public record Shape(List<Integer> keys, List<Shape> children) {
        public Shape {
            keys = List.copyOf(keys);
            children = List.copyOf(children);
        }
    }

    private static final class Node {
        private final List<Integer> keys = new ArrayList<>(MAX_KEYS);
        private final List<Node> children = new ArrayList<>(MAX_KEYS + 1);

        private int position(int key) {
            int index = 0;
            while (index < keys.size() && keys.get(index) < key) {
                index++;
            }
            return index;
        }

        private boolean matches(int index, int key) {
            return index < keys.size() && keys.get(index) == key;
        }

        private boolean isLeaf() {
            return children.isEmpty();
        }

        private boolean isFull() {
            return keys.size() == MAX_KEYS;
        }

        private void insert(int key) {
            int index = position(key);
            if (isLeaf()) {
                keys.add(index, key);
                return;
            }
            if (children.get(index).isFull()) {
                splitChild(index);
                if (key > keys.get(index)) {
                    index++;
                }
            }
            children.get(index).insert(key);
        }

        private void splitChild(int index) {
            Node left = children.get(index);
            Node right = new Node();
            // Из трёх ключей крайние остаются в половинах, средний уходит в родителя.
            right.keys.add(left.keys.remove(MEDIAN + 1));
            keys.add(index, left.keys.remove(MEDIAN));
            if (!left.isLeaf()) {
                List<Node> rightChildren = left.children.subList(MEDIAN + 1, left.children.size());
                right.children.addAll(rightChildren);
                rightChildren.clear();
            }
            children.add(index + 1, right);
        }

        private Shape snapshot() {
            return new Shape(keys, children.stream().map(Node::snapshot).toList());
        }
    }
}
