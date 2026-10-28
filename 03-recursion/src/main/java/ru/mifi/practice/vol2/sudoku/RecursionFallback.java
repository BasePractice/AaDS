package ru.mifi.practice.vol2.sudoku;

/** Перебор с возвратом: первая пустая клетка заполняется первой подходящей цифрой. */
final class RecursionFallback implements Sudoku {
    private final Block grid;
    private final boolean debug;
    private int deep;
    private int iterations;

    RecursionFallback(int size, int[][] values, boolean debug) {
        this(Block.of(size, values), debug);
    }

    RecursionFallback(Block grid, boolean debug) {
        this.grid = grid;
        this.debug = debug;
    }

    @Override
    public boolean solve() {
        ++deep;
        for (int row = 0; row < grid.size(); row++) {
            ++iterations;
            for (int col = 0; col < grid.size(); col++) {
                ++iterations;
                if (grid.at(row, col).equals(Value.EMPTY)) {
                    for (int iTry = 1; iTry <= grid.size(); iTry++) {
                        Value digit = Value.DIGITS[iTry];
                        ++iterations;
                        if (isPlacement(row, col, digit)) {
                            grid.set(row, col, digit);
                            printDeep(row, col, digit);
                            if (solve()) {
                                --deep;
                                return true;
                            } else {
                                grid.set(row, col, Value.EMPTY);
                            }
                        }
                    }
                    --deep;
                    return false;
                }
            }
        }
        --deep;
        return true;
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

    private boolean isPlacement(int row, int col, Value digit) {
        boolean valid = !grid.isNumberInRow(row, digit)
            && !grid.isNumberInCol(col, digit)
            && !grid.isNumberInQuad(row, col, digit);
        iterations += grid.iterations();
        grid.clear();
        return valid;
    }

    private void printDeep(int row, int col, Value digit) {
        if (debug && deep >= 59) {
            grid.print(String.format("%2d] %d:%d = %s", deep, row + 1, col + 1, digit));
        }
    }
}
