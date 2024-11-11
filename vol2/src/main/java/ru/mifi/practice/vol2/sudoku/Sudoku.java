package ru.mifi.practice.vol2.sudoku;

public interface Sudoku {

    static Sudoku recursionFallback(int size, int[][] values) {
        return new RecursionFallback(size, values);
    }

    static Factory recursionFactory() {
        return RecursionFallback::new;
    }

    boolean solve();

    default void print() {
        print("");
    }

    void print(String title);

    int iterations();

    void clear();

    @FunctionalInterface
    interface Factory {
        Sudoku create(int size, int[][] values);
    }

    abstract class AbstractSudoku implements Sudoku {
        protected final Block grid;
        protected int deep = 0;
        protected int iterations = 0;

        protected AbstractSudoku(Block grid) {
            this.grid = grid;
        }

        @Override
        public void print(String title) {
            grid.print(title);
        }

        @Override
        public int iterations() {
            return iterations;
        }

        @Override
        public void clear() {
            deep = 0;
            iterations = 0;
        }

        protected boolean isPlacement(int row, int col, Value digit) {
            return !grid.isNumberInRow(row, digit)
                && !grid.isNumberInCol(col, digit)
                && !grid.isNumberInQuad(row, col, digit);
        }
    }
}
