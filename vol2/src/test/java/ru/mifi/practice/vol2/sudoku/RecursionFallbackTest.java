package ru.mifi.practice.vol2.sudoku;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@DisplayName("Решение судоку перебором с возвратом")
final class RecursionFallbackTest {

    @DisplayName("Классическая задача решается до конца")
    @Test
    @Timeout(1)
    void solvesTheClassicPuzzle() {
        int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9},
        };
        assertThat("the classic puzzle stays unsolved",
            Sudoku.recursionFactory(false).create(puzzle.length, puzzle).solve(), is(true));
    }

    @DisplayName("Решение классической задачи совпадает с единственно верным")
    @Test
    @Timeout(1)
    void reachesTheOnlyValidSolutionOfTheClassicPuzzle() {
        int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9},
        };
        Block grid = Block.of(puzzle.length, puzzle);
        new RecursionFallback(grid, false).solve();
        StringBuilder solution = new StringBuilder();
        for (int row = 0; row < grid.size(); row++) {
            for (int col = 0; col < grid.size(); col++) {
                solution.append(grid.at(row, col));
            }
        }
        assertThat("the solved grid dont match the only valid solution", solution.toString(),
            is("534678912"
                + "672195348"
                + "198342567"
                + "859761423"
                + "426853791"
                + "713924856"
                + "961537284"
                + "287419635"
                + "345286179"));
    }

    @DisplayName("Пустое поле заполняется без повторов в квадранте")
    @Test
    @Timeout(1)
    void fillsAnEmptyGridWithoutRepeatsInAQuadrant() {
        Block grid = Block.of(9, new int[9][9]);
        new RecursionFallback(grid, false).solve();
        StringBuilder quadrant = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                quadrant.append(grid.at(row, col));
            }
        }
        assertThat("the first quadrant of an empty grid repeats a digit",
            quadrant.toString(), is("123456789"));
    }

    @DisplayName("Клетка без единого кандидата обрывает перебор")
    @Test
    @Timeout(1)
    void rejectsAPuzzleWithoutACandidate() {
        int[][] puzzle = new int[9][9];
        for (int col = 0; col < 8; col++) {
            puzzle[0][col] = col + 1;
        }
        puzzle[1][8] = 9;
        assertThat("a cell without a candidate is reported as solvable",
            Sudoku.recursionFactory(false).create(puzzle.length, puzzle).solve(), is(false));
    }

    @DisplayName("Перебор считает свои итерации")
    @Test
    @Timeout(1)
    void countsItsOwnIterations() {
        int[][] puzzle = new int[9][9];
        Sudoku sudoku = Sudoku.recursionFactory(false).create(puzzle.length, puzzle);
        sudoku.solve();
        assertThat("the search dont count its iterations", sudoku.iterations(), is(greaterThan(0)));
    }

    @DisplayName("Сброс обнуляет счётчик итераций")
    @Test
    @Timeout(1)
    void clearResetsTheIterationCounter() {
        int[][] puzzle = new int[9][9];
        Sudoku sudoku = Sudoku.recursionFactory(false).create(puzzle.length, puzzle);
        sudoku.solve();
        sudoku.clear();
        assertThat("clear dont reset the iteration counter", sudoku.iterations(), is(0));
    }
}
