package ru.mifi.practice.boring.structure;

/**
 * Разреженная таблица: отвечает на запрос минимума на отрезке за O(1), но только пока массив не
 * меняется. В строке k таблицы лежат минимумы всех отрезков длины 2^k, и любой отрезок
 * накрывается двумя такими — от левого края и от правого. Они перекрываются, но минимуму это
 * безразлично: он идемпотентен, лишний раз учтённый элемент ответа не портит.
 *
 * <p>Дерево отрезков умеет то же самое за O(log n) и переживает изменения; разреженная таблица
 * меняться не умеет вовсе, зато отвечает мгновенно. Предобработка стоит O(n log n) времени и
 * столько же памяти.
 *
 * <p>Именно из-за перекрытия таблицу нельзя строить на сумме: она посчитала бы общую часть
 * дважды. Минимум, максимум и наибольший общий делитель — можно.
 */
public final class SparseTable {
    private final int[][] minimums;
    private final int[] logarithms;

    public SparseTable(int[] values) {
        this.logarithms = logarithms(values.length);
        this.minimums = minimums(values);
    }

    /** Минимум на полуинтервале от from включительно до to исключительно. */
    public int minimum(int from, int to) {
        if (from >= to) {
            throw new IllegalArgumentException("Отрезок от " + from + " до " + to + " пуст, минимума в нём нет");
        }
        int row = logarithms[to - from];
        return Math.min(minimums[row][from], minimums[row][to - (1 << row)]);
    }

    private int[][] minimums(int[] values) {
        int rows = logarithms[values.length] + 1;
        int[][] minimums = new int[rows][values.length];
        System.arraycopy(values, 0, minimums[0], 0, values.length);
        for (int row = 1; row < rows; row++) {
            int length = 1 << row;
            for (int from = 0; from + length <= values.length; from++) {
                minimums[row][from] = Math.min(minimums[row - 1][from], minimums[row - 1][from + length / 2]);
            }
        }
        return minimums;
    }

    private int[] logarithms(int length) {
        int[] logarithms = new int[length + 1];
        for (int value = 2; value <= length; value++) {
            logarithms[value] = logarithms[value / 2] + 1;
        }
        return logarithms;
    }
}
