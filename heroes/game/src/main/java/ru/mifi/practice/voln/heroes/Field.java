package ru.mifi.practice.voln.heroes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Геометрия поля боя: где лежат камни, где стоят отряды, куда пеший дойдёт и куда долетит стрела.
 *
 * <p>Одна и та же геометрия нужна двоим: живому полю боя, которое ходит и рисует, и снимку
 * позиции, по которому тактика перебирает варианты. Поэтому расчёт вынесен из поля боя сюда и
 * не знает ни об очереди ходов, ни о здоровье отрядов — только о том, что клетка занята.
 */
final class Field {
    private static final int[] NEIGHBOUR_ROWS = {0, 0, 1, -1};
    private static final int[] NEIGHBOUR_COLUMNS = {1, -1, 0, 0};

    private final boolean[][] stones;
    private final boolean[][] troops;

    Field(boolean[][] stones, boolean[][] troops) {
        this.stones = copy(stones);
        this.troops = copy(troops);
    }

    /** Лежит ли клетка внутри поля боя. */
    static boolean isInside(int row, int column) {
        return row >= 0 && row < Constants.ROWS && column >= 0 && column < Constants.COLS;
    }

    /** Расстояние по прямой: шаг наискось стоит столько же, сколько прямой. */
    static int distance(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        return Math.max(Math.abs(targetRow - sourceRow), Math.abs(targetColumn - sourceColumn));
    }

    private static boolean[][] copy(boolean[][] cells) {
        boolean[][] copy = new boolean[Constants.ROWS][];
        for (int row = 0; row < Constants.ROWS; row++) {
            copy[row] = Arrays.copyOf(cells[row], Constants.COLS);
        }
        return copy;
    }

    boolean isStone(int row, int column) {
        return stones[row][column];
    }

    boolean isTroop(int row, int column) {
        return troops[row][column];
    }

    /** Стоит ли рядом с клеткой хоть кто-нибудь из тех, чьи клетки перечислены. */
    boolean adjoins(int row, int column, boolean[][] cells) {
        for (int i = 0; i < NEIGHBOUR_ROWS.length; i++) {
            int nextRow = row + NEIGHBOUR_ROWS[i];
            int nextColumn = column + NEIGHBOUR_COLUMNS[i];
            if (isInside(nextRow, nextColumn) && cells[nextRow][nextColumn]) {
                return true;
            }
        }
        return false;
    }

    int[][] distances(int row, int column, boolean flying) {
        return distances(row, column, flying, -1, -1);
    }

    /**
     * Обход поля в ширину от клетки: сколько шагов до каждой клетки. Чужая клетка непроходима,
     * а камень пеший обходит, тогда как летун проходит над ним. Клетка цели считается проходимой,
     * иначе до противника не дошагать вовсе.
     */
    int[][] distances(int row, int column, boolean flying, int targetRow, int targetColumn) {
        int[][] distances = new int[Constants.ROWS][Constants.COLS];
        for (int[] line : distances) {
            Arrays.fill(line, Integer.MAX_VALUE);
        }
        distances[row][column] = 0;
        Queue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(cell -> cell[2]));
        queue.add(new int[]{row, column, 0});
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[2] > distances[current[0]][current[1]]) {
                continue;
            }
            for (int i = 0; i < NEIGHBOUR_ROWS.length; i++) {
                int nextRow = current[0] + NEIGHBOUR_ROWS[i];
                int nextColumn = current[1] + NEIGHBOUR_COLUMNS[i];
                if (!isInside(nextRow, nextColumn)
                    || troops[nextRow][nextColumn] && (nextRow != targetRow || nextColumn != targetColumn)
                    || !flying && stones[nextRow][nextColumn]) {
                    continue;
                }
                int next = current[2] + 1;
                if (next < distances[nextRow][nextColumn]) {
                    distances[nextRow][nextColumn] = next;
                    queue.add(new int[]{nextRow, nextColumn, next});
                }
            }
        }
        return distances;
    }

    /**
     * Кратчайший путь от клетки до клетки вместе с обеими концами или пустой список, если пути
     * нет. Путь восстанавливается с конца по разметке обхода: каждый шаг назад ведёт в соседа,
     * до которого на один шаг ближе.
     */
    List<int[]> path(int sourceRow, int sourceColumn, int targetRow, int targetColumn, boolean flying) {
        int[][] distances = distances(sourceRow, sourceColumn, flying, targetRow, targetColumn);
        if (distances[targetRow][targetColumn] == Integer.MAX_VALUE) {
            return new ArrayList<>();
        }
        List<int[]> path = new ArrayList<>();
        int currentRow = targetRow;
        int currentColumn = targetColumn;
        path.add(new int[]{currentRow, currentColumn});
        while (currentRow != sourceRow || currentColumn != sourceColumn) {
            int[] step = back(distances, currentRow, currentColumn);
            if (step.length != 2) {
                return List.of();
            }
            currentRow = step[0];
            currentColumn = step[1];
            path.add(0, new int[]{currentRow, currentColumn});
        }
        return path;
    }

    /** Сосед, до которого на один шаг ближе, или пустая пара, если такого нет. */
    private int[] back(int[][] distances, int row, int column) {
        for (int i = 0; i < NEIGHBOUR_ROWS.length; i++) {
            int nextRow = row + NEIGHBOUR_ROWS[i];
            int nextColumn = column + NEIGHBOUR_COLUMNS[i];
            if (isInside(nextRow, nextColumn) && distances[nextRow][nextColumn] == distances[row][column] - 1) {
                return new int[]{nextRow, nextColumn};
            }
        }
        return new int[0];
    }

    /** Видно ли цель: стрела летит по прямой и втыкается в первый же камень на пути. */
    boolean visible(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        int steps = distance(sourceRow, sourceColumn, targetRow, targetColumn);
        for (int step = 1; step < steps; step++) {
            int row = sourceRow + (targetRow - sourceRow) * step / steps;
            int column = sourceColumn + (targetColumn - sourceColumn) * step / steps;
            if (stones[row][column]) {
                return false;
            }
        }
        return true;
    }
}
