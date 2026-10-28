package ru.mifi.practice.boring.dynamic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Задача о назначениях венгерским алгоритмом")
final class HungarianTest {

    @DisplayName("Находит наименьшую суммарную стоимость")
    @Test
    @Timeout(1)
    void findsTheCheapestAssignment() {
        int[][] costs = {{4, 1, 3}, {2, 0, 5}, {3, 2, 2}};
        Hungarian hungarian = new Hungarian();
        assertThat("the assignment is not the cheapest",
            hungarian.cost(costs, hungarian.assignment(costs)), is(5));
    }

    @DisplayName("Каждой строке достаётся свой столбец")
    @Test
    @Timeout(1)
    void givesEveryRowItsOwnColumn() {
        int[][] costs = {{4, 1, 3}, {2, 0, 5}, {3, 2, 2}};
        assertThat("a column is given to two rows",
            Arrays.stream(new Hungarian().assignment(costs)).distinct().count(), is(3L));
    }

    @DisplayName("Жадный выбор проигрывает венгерскому")
    @Test
    @Timeout(1)
    void beatsTheGreedyChoice() {
        int[][] costs = {{1, 2}, {1, 100}};
        Hungarian hungarian = new Hungarian();
        assertThat("the greedy choice wins over the hungarian algorithm",
            hungarian.cost(costs, hungarian.assignment(costs)), is(3));
    }

    @DisplayName("Матрица один на один назначается сама собой")
    @Test
    @Timeout(1)
    void assignsTheSingleCell() {
        assertThat("the single cell is assigned wrong",
            new Hungarian().assignment(new int[][]{{7}}), is(new int[]{0}));
    }

    @DisplayName("Находит то же, что полный перебор перестановок")
    @Test
    @Timeout(10)
    void agreesWithThePlainSearch() {
        Random random = new Random(20260815L);
        Hungarian hungarian = new Hungarian();
        int mismatches = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            int size = 1 + random.nextInt(6);
            int[][] costs = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    costs[row][column] = random.nextInt(50);
                }
            }
            if (hungarian.cost(costs, hungarian.assignment(costs)) != brute(costs)) {
                ++mismatches;
            }
        }
        assertThat("the hungarian algorithm disagrees with the plain search", mismatches, is(0));
    }

    private static int brute(int[][] costs) {
        List<Integer> columns = new ArrayList<>();
        for (int column = 0; column < costs.length; column++) {
            columns.add(column);
        }
        return cheapest(costs, columns, 0);
    }

    private static int cheapest(int[][] costs, List<Integer> rest, int row) {
        if (rest.isEmpty()) {
            return 0;
        }
        int cheapest = Integer.MAX_VALUE;
        for (int at = 0; at < rest.size(); at++) {
            List<Integer> without = new ArrayList<>(rest);
            int column = without.remove(at);
            cheapest = Math.min(cheapest, costs[row][column] + cheapest(costs, without, row + 1));
        }
        return cheapest;
    }
}
