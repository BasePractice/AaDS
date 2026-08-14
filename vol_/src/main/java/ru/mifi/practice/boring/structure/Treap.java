package ru.mifi.practice.boring.structure;

import java.util.OptionalInt;
import java.util.Random;

/**
 * Декартово дерево: одновременно дерево поиска по ключу и куча по случайному приоритету. Ключи
 * упорядочены слева направо, приоритеты убывают сверху вниз — и этих двух условий хватает, чтобы
 * форма дерева определялась однозначно.
 *
 * <p>Отсюда и балансировка: приоритеты случайны, поэтому дерево получается таким, каким вышло бы
 * обычное дерево поиска при случайном порядке вставок, — глубина O(log n) в среднем. Никаких
 * поворотов, цветов и коэффициентов баланса, как в AVL или красно-чёрном дереве: вся работа
 * делается двумя операциями.
 *
 * <p>Разрезание делит дерево по ключу на два, слияние соединяет два дерева, где все ключи левого
 * меньше всех ключей правого. Вставка — это разрезать и слить обратно вместе с новым узлом,
 * удаление — слить двух детей вместо узла. Сорок строк на всё.
 */
public final class Treap implements Ordered {
    private final Random random;
    private Node root;
    private int size;

    public Treap() {
        this(new Random());
    }

    public Treap(Random random) {
        this.random = random;
    }

    @Override
    public void add(int value) {
        if (contains(value)) {
            return;
        }
        Halves halves = split(root, value);
        root = merge(merge(halves.left, new Node(value, random.nextInt())), halves.right);
        ++size;
    }

    @Override
    public void remove(int value) {
        if (contains(value)) {
            root = removed(root, value);
            --size;
        }
    }

    @Override
    public boolean contains(int value) {
        Node current = root;
        while (current != null) {
            if (current.key == value) {
                return true;
            }
            current = value < current.key ? current.left : current.right;
        }
        return false;
    }

    @Override
    public OptionalInt next(int value) {
        OptionalInt found = OptionalInt.empty();
        Node current = root;
        while (current != null) {
            if (current.key > value) {
                found = OptionalInt.of(current.key);
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return found;
    }

    @Override
    public OptionalInt previous(int value) {
        OptionalInt found = OptionalInt.empty();
        Node current = root;
        while (current != null) {
            if (current.key < value) {
                found = OptionalInt.of(current.key);
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return found;
    }

    @Override
    public int size() {
        return size;
    }

    private Node removed(Node node, int value) {
        if (node.key == value) {
            return merge(node.left, node.right);
        }
        if (value < node.key) {
            node.left = removed(node.left, value);
        } else {
            node.right = removed(node.right, value);
        }
        return node;
    }

    /** Делит дерево на ключи меньше границы и все остальные, сохраняя оба свойства дерева. */
    private Halves split(Node node, int border) {
        if (node == null) {
            return new Halves(null, null);
        }
        if (node.key < border) {
            Halves halves = split(node.right, border);
            node.right = halves.left;
            return new Halves(node, halves.right);
        }
        Halves halves = split(node.left, border);
        node.left = halves.right;
        return new Halves(halves.left, node);
    }

    /** Сливает два дерева, у которых все ключи левого меньше всех ключей правого. */
    private Node merge(Node left, Node right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        if (left.priority > right.priority) {
            left.right = merge(left.right, right);
            return left;
        }
        right.left = merge(left, right.left);
        return right;
    }

    @Override
    public String toString() {
        return "декартово дерево из " + size + " чисел";
    }

    /** Две половины разрезанного дерева. */
    private record Halves(Node left, Node right) {
    }

    /** Узел: ключ задаёт порядок дерева поиска, приоритет — порядок кучи. */
    private static final class Node {
        private final int key;
        private final int priority;
        private Node left;
        private Node right;

        Node(int key, int priority) {
            this.key = key;
            this.priority = priority;
        }
    }
}
