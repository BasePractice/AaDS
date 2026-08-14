package ru.mifi.practice.boring.dynamic;

import java.util.Arrays;

/**
 * Венгерский алгоритм: задача о назначениях — раздать n работ n исполнителям так, чтобы суммарная
 * стоимость вышла наименьшей. Перебор всех раскладов стоит n!, жадность ошибается: дешёвое
 * назначение может вынудить дорогое дальше.
 *
 * <p>В основе — наблюдение Кёнига: если из строки матрицы вычесть одно и то же число, оптимальное
 * назначение не изменится, изменится только его стоимость. Алгоритм подбирает такие поправки —
 * потенциалы строк и столбцов, — пока в матрице не наберётся n нулей, стоящих в разных строках и
 * столбцах. Эти нули и есть ответ.
 *
 * <p>Здесь взята форма Джонкера — Волгенанта: строки добавляются по одной, и для каждой ищется
 * увеличивающая цепочка — как в поиске паросочетания, только с учётом потенциалов. Каждая строка
 * стоит O(n²), всего O(n³) — вместо факториала.
 *
 * <p>Индексация внутри с единицы: нулевые строка и столбец служебные, они хранят потенциалы и
 * фиктивное назначение. Так классическая запись алгоритма читается без поправок на смещение.
 */
public final class Hungarian {
    private static final int INFINITY = Integer.MAX_VALUE / 2;

    /** Для каждой строки — назначенный ей столбец. */
    public int[] assignment(int[][] costs) {
        int size = costs.length;
        int[] rowPotentials = new int[size + 1];
        int[] columnPotentials = new int[size + 1];
        int[] assigned = new int[size + 1];
        int[] ways = new int[size + 1];
        for (int row = 1; row <= size; row++) {
            augment(costs, row, rowPotentials, columnPotentials, assigned, ways);
        }
        int[] answer = new int[size];
        for (int column = 1; column <= size; column++) {
            if (assigned[column] > 0) {
                answer[assigned[column] - 1] = column - 1;
            }
        }
        return answer;
    }

    /** Суммарная стоимость назначения. */
    public int cost(int[][] costs, int[] assignment) {
        int cost = 0;
        for (int row = 0; row < assignment.length; row++) {
            cost += costs[row][assignment[row]];
        }
        return cost;
    }

    /** Увеличивающая цепочка для очередной строки с пересчётом потенциалов по пути. */
    private void augment(int[][] costs, int row, int[] rows, int[] columns, int[] assigned, int[] ways) {
        int size = costs.length;
        assigned[0] = row;
        int column = 0;
        int[] slack = new int[size + 1];
        boolean[] used = new boolean[size + 1];
        Arrays.fill(slack, INFINITY);
        do {
            used[column] = true;
            int current = assigned[column];
            int delta = INFINITY;
            int next = 0;
            for (int candidate = 1; candidate <= size; candidate++) {
                if (used[candidate]) {
                    continue;
                }
                int cost = costs[current - 1][candidate - 1] - rows[current] - columns[candidate];
                if (cost < slack[candidate]) {
                    slack[candidate] = cost;
                    ways[candidate] = column;
                }
                if (slack[candidate] < delta) {
                    delta = slack[candidate];
                    next = candidate;
                }
            }
            shift(delta, size, used, rows, columns, slack, assigned);
            column = next;
        } while (assigned[column] != 0);
        while (column != 0) {
            int previous = ways[column];
            assigned[column] = assigned[previous];
            column = previous;
        }
    }

    /** Сдвиг потенциалов на найденную поправку: пройденные строки дешевеют, столбцы дорожают. */
    private void shift(int delta, int size, boolean[] used, int[] rows, int[] columns, int[] slack, int[] assigned) {
        for (int candidate = 0; candidate <= size; candidate++) {
            if (used[candidate]) {
                rows[assigned[candidate]] += delta;
                columns[candidate] -= delta;
            } else {
                slack[candidate] -= delta;
            }
        }
    }
}
