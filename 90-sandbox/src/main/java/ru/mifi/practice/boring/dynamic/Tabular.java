package ru.mifi.practice.boring.dynamic;

/**
 * Наибольшая общая подпоследовательность обычным динамическим программированием. В ячейке (i, j)
 * лежит длина ответа для префиксов длины i и j: если последние символы совпали — единица плюс
 * ответ для укороченных префиксов, иначе — лучший из двух соседей.
 *
 * <p>Сама подпоследовательность восстанавливается обратным ходом от правого нижнего угла: шаг по
 * диагонали означает совпавший символ, шаг вверх или влево — пропущенный.
 *
 * <p>Цена — O(n·m) времени и столько же памяти; память здесь и есть слабое место, ради которого
 * придуман Хиршберг.
 */
public final class Tabular implements Subsequence {

    @Override
    public String longest(String left, String right) {
        int[][] table = table(left, right);
        StringBuilder common = new StringBuilder();
        int row = left.length();
        int column = right.length();
        while (row > 0 && column > 0) {
            if (left.charAt(row - 1) == right.charAt(column - 1)) {
                common.append(left.charAt(row - 1));
                --row;
                --column;
            } else if (table[row - 1][column] >= table[row][column - 1]) {
                --row;
            } else {
                --column;
            }
        }
        return common.reverse().toString();
    }

    private int[][] table(String left, String right) {
        int[][] table = new int[left.length() + 1][right.length() + 1];
        for (int row = 1; row <= left.length(); row++) {
            for (int column = 1; column <= right.length(); column++) {
                table[row][column] = left.charAt(row - 1) == right.charAt(column - 1)
                    ? table[row - 1][column - 1] + 1
                    : Math.max(table[row - 1][column], table[row][column - 1]);
            }
        }
        return table;
    }
}
