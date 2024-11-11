package ru.mifi.practice.vol2.sudoku;

public interface Sudoku {

    static Factory recursionFactory(boolean debug) {
        return (size, values) -> new RecursionFallback(size, values, debug);
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
        protected final boolean debug;
        protected int deep = 0;
        protected int iterations = 0;

        protected AbstractSudoku(Block grid, boolean debug) {
            this.grid = grid;
            this.debug = debug;
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

        protected void printDeep(int row, int col, Value digit) {
            if (debug && deep >= 59) {
                grid.print(String.format("%2d] %d:%d = %s", deep, row + 1, col + 1, digit));
            }
        }
    }
}
