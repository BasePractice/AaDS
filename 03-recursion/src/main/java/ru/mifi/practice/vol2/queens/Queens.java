package ru.mifi.practice.vol2.queens;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;

/**
 * Восемь ферзей: расставить на доске n × n столько же ферзей так, чтобы ни один не бил другого.
 * Расстановка задаётся списком колонок по строкам — по ферзю в строке, поэтому бой по
 * горизонтали исключён самой записью, и остаётся проверять колонку и две диагонали.
 *
 * <p>Ветвление здесь полное — n вариантов в каждой строке, — а спасает отсечение: ветка
 * бросается на первом же бое, не доходя до конца доски. Полный перебор восьмой степени стоил бы
 * шестнадцати миллионов расстановок, отсечение обходится примерно двумя тысячами проверок.
 */
public final class Queens {
    private final int size;

    public Queens(int size) {
        this.size = size;
    }

    public List<List<Integer>> solutions(Counter counter) {
        List<List<Integer>> solutions = new ArrayList<>();
        place(new ArrayList<>(), solutions, counter);
        return solutions;
    }

    private void place(List<Integer> queens, List<List<Integer>> solutions, Counter counter) {
        if (queens.size() == size) {
            solutions.add(List.copyOf(queens));
            return;
        }
        for (int column = 0; column < size; column++) {
            counter.increment();
            if (safe(queens, column)) {
                queens.add(column);
                place(queens, solutions, counter);
                queens.remove(queens.size() - 1);
            }
        }
    }

    private boolean safe(List<Integer> queens, int column) {
        int row = queens.size();
        for (int other = 0; other < row; other++) {
            int distance = Math.abs(queens.get(other) - column);
            if (distance == 0 || distance == row - other) {
                return false;
            }
        }
        return true;
    }
}
