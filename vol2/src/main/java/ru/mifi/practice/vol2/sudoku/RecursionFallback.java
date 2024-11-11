package ru.mifi.practice.vol2.sudoku;

@SuppressWarnings("PMD.UnusedPrivateField")
final class RecursionFallback implements Sudoku {
    private final Block grid;
    private int deep = 0;
    private int iterations = 0;

    RecursionFallback(int size, int[][] values) {
        grid = Block.of(size, values);
    }

    private boolean isPlacement(int row, int col, Value digit) {
        return !grid.isNumberInRow(row, digit)
            && !grid.isNumberInCol(col, digit)
            && !grid.isNumberInQuad(row, col, digit);
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
                            //if (deep >= 59) {
                            //    grid.print(String.format("%2d] %d:%d = %s", deep, row + 1, col + 1, digit));
                            //}
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
}
