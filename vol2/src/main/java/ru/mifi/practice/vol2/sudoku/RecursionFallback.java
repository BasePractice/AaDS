package ru.mifi.practice.vol2.sudoku;

@SuppressWarnings("PMD.UnusedPrivateField")
final class RecursionFallback extends Sudoku.AbstractSudoku {
    RecursionFallback(int size, int[][] values) {
        super(Block.of(size, values));
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
}
