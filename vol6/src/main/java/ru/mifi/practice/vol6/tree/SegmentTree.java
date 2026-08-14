package ru.mifi.practice.vol6.tree;

import java.util.function.IntBinaryOperator;

/**
 * Дерево отрезков: в каждом узле лежит свёртка своей половины массива, поэтому запрос на любом
 * отрезке собирается из O(log n) готовых узлов, а изменение одного значения правит только путь
 * от листа до корня. Прямой перебор отвечает за O(n), предпосчитанные префиксы отвечают за O(1),
 * но ломаются на первом же изменении — дерево отрезков держит и то, и другое.
 *
 * <p>Свёртка задаётся снаружи вместе со своим нейтральным элементом: сумма с нулём, минимум с
 * бесконечностью, наибольший общий делитель с нулём — дерево от этого не меняется.
 */
public final class SegmentTree {
    private final int[] tree;
    private final int size;
    private final IntBinaryOperator fold;
    private final int neutral;

    public SegmentTree(int[] values, IntBinaryOperator fold, int neutral) {
        this.size = values.length;
        this.tree = new int[values.length * 4];
        this.fold = fold;
        this.neutral = neutral;
        build(values, 1, 0, size - 1);
    }

    public int fold(int from, int to) {
        if (from >= to) {
            return neutral;
        }
        return fold(1, 0, size - 1, from, to - 1);
    }

    private int fold(int node, int low, int high, int from, int to) {
        if (from > high || to < low) {
            return neutral;
        }
        if (from <= low && high <= to) {
            return tree[node];
        }
        int middle = (low + high) / 2;
        return fold.applyAsInt(
            fold(node * 2, low, middle, from, to),
            fold(node * 2 + 1, middle + 1, high, from, to));
    }

    public void set(int index, int value) {
        set(1, 0, size - 1, index, value);
    }

    private void set(int node, int low, int high, int index, int value) {
        if (low == high) {
            tree[node] = value;
            return;
        }
        int middle = (low + high) / 2;
        if (index <= middle) {
            set(node * 2, low, middle, index, value);
        } else {
            set(node * 2 + 1, middle + 1, high, index, value);
        }
        tree[node] = fold.applyAsInt(tree[node * 2], tree[node * 2 + 1]);
    }

    private void build(int[] values, int node, int low, int high) {
        if (low == high) {
            tree[node] = values[low];
            return;
        }
        int middle = (low + high) / 2;
        build(values, node * 2, low, middle);
        build(values, node * 2 + 1, middle + 1, high);
        tree[node] = fold.applyAsInt(tree[node * 2], tree[node * 2 + 1]);
    }
}
