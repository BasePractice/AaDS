package ru.mifi.practice.boring.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * X-быстрый бор: числа хранятся не по сравнению, а по битам. Каждое число — путь из w бит от
 * корня к листу, все существующие пути каждой длины лежат в своей хеш-таблице: уровень k знает
 * все k-битные префиксы хранимых чисел.
 *
 * <p>Отсюда трюк: самый длинный существующий префикс искомого числа находится двоичным поиском
 * по уровням — O(log w) обращений к хеш-таблице вместо w шагов спуска. Для 32-битных чисел это
 * пять проверок, и время не зависит от того, сколько чисел лежит в структуре, — тем бор и
 * отличается от дерева поиска.
 *
 * <p>Дальше нужен сосед. У каждого внутреннего узла хранится нить: если у него нет левого сына,
 * нить показывает на наименьший лист поддерева, если нет правого — на наибольший. По нити
 * попадаем на ближайший лист, а листья связаны в двусвязный список, поэтому предыдущий и
 * следующий берутся оттуда за шаг.
 *
 * <p>Плата — память: хеш-таблица на каждый уровень, то есть O(n·w) вместо O(n) у дерева.
 * Ширина числа здесь урезана до 16 бит, чтобы в тестах было видно и переполнение уровней, и
 * работу нитей; идея от ширины не зависит.
 */
public final class XFastTrie implements Ordered {
    private static final int WIDTH = 16;
    private static final int LIMIT = 1 << WIDTH;
    private final Map<Integer, Node>[] levels = levels();
    private final Map<Integer, Leaf> leaves = new HashMap<>();

    @Override
    public void add(int value) {
        checkRange(value);
        if (leaves.containsKey(value)) {
            return;
        }
        Leaf leaf = new Leaf(value);
        link(leaf);
        leaves.put(value, leaf);
        levels[WIDTH].put(value, new Node(leaf, leaf));
        for (int level = WIDTH - 1; level >= 0; level--) {
            grow(value >>> WIDTH - level, level, leaf);
        }
    }

    @Override
    public void remove(int value) {
        Leaf leaf = leaves.remove(value);
        if (leaf == null) {
            return;
        }
        unlink(leaf);
        for (int level = WIDTH; level >= 0; level--) {
            shrink(value >>> WIDTH - level, level);
        }
    }

    @Override
    public boolean contains(int value) {
        return leaves.containsKey(value);
    }

    @Override
    public OptionalInt next(int value) {
        Leaf nearest = nearest(value);
        if (nearest == null) {
            return OptionalInt.empty();
        }
        Leaf found = nearest.key > value ? nearest : nearest.next;
        return found == null ? OptionalInt.empty() : OptionalInt.of(found.key);
    }

    @Override
    public OptionalInt previous(int value) {
        Leaf nearest = nearest(value);
        if (nearest == null) {
            return OptionalInt.empty();
        }
        Leaf found = nearest.key < value ? nearest : nearest.previous;
        return found == null ? OptionalInt.empty() : OptionalInt.of(found.key);
    }

    @Override
    public int size() {
        return leaves.size();
    }

    /**
     * Лист, ближайший к числу по нити самого длинного существующего префикса. Может лежать как
     * слева от числа, так и справа, — какой именно, разбирают next и previous.
     */
    private Leaf nearest(int value) {
        if (leaves.isEmpty()) {
            return null;
        }
        int level = longest(value);
        Node node = levels[level].get(value >>> WIDTH - level);
        return node.left.key > value ? node.left : node.right;
    }

    /** Двоичный поиск по уровням: самая длинная длина префикса, который в боре есть. */
    private int longest(int value) {
        int low = 0;
        int high = WIDTH;
        while (low < high) {
            int middle = (low + high + 1) / 2;
            if (levels[middle].containsKey(value >>> WIDTH - middle)) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        return low;
    }

    private void grow(int prefix, int level, Leaf leaf) {
        Node node = levels[level].get(prefix);
        if (node == null) {
            levels[level].put(prefix, new Node(leaf, leaf));
            return;
        }
        if (leaf.key < node.left.key) {
            node.left = leaf;
        }
        if (leaf.key > node.right.key) {
            node.right = leaf;
        }
    }

    private void shrink(int prefix, int level) {
        if (level == WIDTH || childless(prefix, level)) {
            levels[level].remove(prefix);
            return;
        }
        Node node = levels[level].get(prefix);
        node.left = edge(prefix, level, true);
        node.right = edge(prefix, level, false);
    }

    private boolean childless(int prefix, int level) {
        return !levels[level + 1].containsKey(prefix * 2) && !levels[level + 1].containsKey(prefix * 2 + 1);
    }

    /**
     * Крайний лист поддерева префикса: спуск всегда в сторону нужного края, а если там ветки
     * нет — в другую. Ровно w шагов, никакого перебора чисел.
     */
    private Leaf edge(int prefix, int level, boolean smallest) {
        int current = prefix;
        for (int depth = level; depth < WIDTH; depth++) {
            int wanted = current * 2 + (smallest ? 0 : 1);
            int other = current * 2 + (smallest ? 1 : 0);
            current = levels[depth + 1].containsKey(wanted) ? wanted : other;
        }
        return leaves.get(current);
    }

    /** Вставляет лист в двусвязный список: соседи находятся по нити, а не перебором. */
    private void link(Leaf leaf) {
        Leaf near = nearest(leaf.key);
        if (near == null) {
            return;
        }
        Leaf previous = near.key < leaf.key ? near : near.previous;
        Leaf next = previous == null ? near : previous.next;
        leaf.previous = previous;
        leaf.next = next;
        if (previous != null) {
            previous.next = leaf;
        }
        if (next != null) {
            next.previous = leaf;
        }
    }

    private void unlink(Leaf leaf) {
        if (leaf.previous != null) {
            leaf.previous.next = leaf.next;
        }
        if (leaf.next != null) {
            leaf.next.previous = leaf.previous;
        }
    }

    private void checkRange(int value) {
        if (value < 0 || value >= LIMIT) {
            throw new IllegalArgumentException("Бор хранит числа от 0 до " + (LIMIT - 1) + ", а получил " + value);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Node>[] levels() {
        Map<Integer, Node>[] levels = new Map[WIDTH + 1];
        for (int level = 0; level <= WIDTH; level++) {
            levels[level] = new HashMap<>();
        }
        return levels;
    }

    @Override
    public String toString() {
        return "X-быстрый бор из " + leaves.size() + " чисел";
    }

    /** Узел уровня: нити на крайние листья своего поддерева. */
    private static final class Node {
        private Leaf left;
        private Leaf right;

        Node(Leaf left, Leaf right) {
            this.left = left;
            this.right = right;
        }
    }

    /** Лист: само число и соседи по двусвязному списку всех листьев. */
    private static final class Leaf {
        private final int key;
        private Leaf previous;
        private Leaf next;

        Leaf(int key) {
            this.key = key;
        }
    }
}
