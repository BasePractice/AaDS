package ru.mifi.practice.vol6.tree;

/**
 * Дерево Фенвика: то же, что дерево отрезков, но только для сумм и вчетверо дешевле по памяти —
 * узлов ровно n, а не 4n. Узел с номером i хранит сумму последних i & −i элементов своего
 * префикса, поэтому и сумма префикса, и прибавка к элементу проходят по O(log n) узлам, сбрасывая
 * младший единичный бит номера или добавляя его.
 *
 * <p>Дерева как связной структуры здесь нет вовсе — есть массив, в котором номер узла сам
 * задаёт длину покрытого им отрезка. Сумма отрезка считается разностью двух префиксов.
 */
public final class FenwickTree {
    private final int[] sums;

    public FenwickTree(int[] values) {
        this.sums = new int[values.length + 1];
        for (int index = 0; index < values.length; index++) {
            add(index, values[index]);
        }
    }

    public void add(int index, int delta) {
        for (int node = index + 1; node < sums.length; node += node & -node) {
            sums[node] += delta;
        }
    }

    public int prefix(int count) {
        int sum = 0;
        for (int node = count; node > 0; node -= node & -node) {
            sum += sums[node];
        }
        return sum;
    }

    public int sum(int from, int to) {
        return prefix(to) - prefix(from);
    }
}
