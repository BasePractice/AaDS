package ru.mifi.practice.boring.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Биномиальная куча: не одно дерево, а список деревьев строго определённой формы. Биномиальное
 * дерево порядка k состоит из двух деревьев порядка k−1, одно из которых подвешено к корню
 * другого, и содержит ровно 2^k узлов. Поэтому куча из n элементов раскладывается по деревьям
 * так же, как n раскладывается по степеням двойки: дерево порядка k есть тогда, когда в двоичной
 * записи n стоит единица в разряде k.
 *
 * <p>Отсюда и слияние двух куч за O(log n) — то, чего обычная двоичная куча не умеет: слить её с
 * такой же можно только пересыпав элементы по одному. Здесь же деревья складываются точно так же,
 * как складываются двоичные числа: два дерева одного порядка дают одно дерево следующего
 * порядка — это перенос в старший разряд.
 *
 * <p>Вставка — слияние с кучей из одного дерева, извлечение минимума — выбросить корень
 * минимального дерева и слить его детей обратно. Всё за O(log n).
 */
public final class BinomialHeap {
    private final List<Node> trees;

    public BinomialHeap() {
        this(new ArrayList<>());
    }

    private BinomialHeap(List<Node> trees) {
        this.trees = trees;
    }

    public void add(int value) {
        merge(new BinomialHeap(new ArrayList<>(List.of(new Node(value, 0, List.of())))));
    }

    /** Забирает деревья чужой кучи себе, оставляя её пустой: сливать можно только раз. */
    public void merge(BinomialHeap other) {
        List<Node> merged = new ArrayList<>(trees);
        merged.addAll(other.trees);
        other.trees.clear();
        trees.clear();
        trees.addAll(carried(merged));
    }

    public int peek() {
        return smallest().value;
    }

    public int poll() {
        Node smallest = smallest();
        trees.remove(smallest);
        merge(new BinomialHeap(new ArrayList<>(smallest.children)));
        return smallest.value;
    }

    public int size() {
        int size = 0;
        for (Node tree : trees) {
            size += 1 << tree.order;
        }
        return size;
    }

    public boolean isEmpty() {
        return trees.isEmpty();
    }

    /** Складывает деревья как двоичные числа: два одного порядка дают одно следующего. */
    private List<Node> carried(List<Node> merged) {
        merged.sort((left, right) -> Integer.compare(left.order, right.order));
        List<Node> carried = new ArrayList<>();
        for (Node tree : merged) {
            Node current = tree;
            while (!carried.isEmpty() && carried.get(carried.size() - 1).order == current.order) {
                current = joined(carried.remove(carried.size() - 1), current);
            }
            carried.add(current);
        }
        return carried;
    }

    /** Подвешивает большее дерево к корню меньшего — порядок растёт на единицу. */
    private Node joined(Node left, Node right) {
        Node parent = left.value <= right.value ? left : right;
        Node child = left.value <= right.value ? right : left;
        List<Node> children = new ArrayList<>(parent.children);
        children.add(child);
        return new Node(parent.value, parent.order + 1, children);
    }

    private Node smallest() {
        if (trees.isEmpty()) {
            throw new NoSuchElementException("Куча пуста");
        }
        Node smallest = trees.get(0);
        for (Node tree : trees) {
            if (tree.value < smallest.value) {
                smallest = tree;
            }
        }
        return smallest;
    }

    @Override
    public String toString() {
        return "биномиальная куча из " + size() + " чисел в " + trees.size() + " деревьях";
    }

    /** Дерево: значение корня, порядок и поддеревья порядков от нуля до порядка минус один. */
    private record Node(int value, int order, List<Node> children) {
    }
}
