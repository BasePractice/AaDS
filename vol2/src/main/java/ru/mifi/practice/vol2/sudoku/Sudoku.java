package ru.mifi.practice.vol2.sudoku;

import java.util.Objects;

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

    interface Block {
        static Block of(int size, int[][] values) {
            return new Default(size, values);
        }

        int size();

        Value at(int row, int col);

        boolean isNumberInRow(int row, Value digit);

        boolean isNumberInCol(int col, Value digit);

        boolean isNumberInQuad(int row, int col, Value digit);

        void set(int row, int col, Value digit);

        default void print() {
            print("");
        }

        void print(String title);

        final class Default implements Block {
            private final Value[][] grid;
            private final int half;

            private Default(int size) {
                grid = new Value[size][size];
                half = 3;
            }

            private Default(int size, int[] values) {
                this(size);
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        grid[row][col] = Value.DIGITS[values[row * size + col]];
                    }
                }
            }

            private Default(int size, int[][] values) {
                this(size);
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        grid[row][col] = Value.DIGITS[values[row][col]];
                    }
                }
            }

            @Override
            public int size() {
                return grid.length;
            }

            @Override
            public Value at(int row, int col) {
                return grid[row][col];
            }

            @Override
            public boolean isNumberInRow(int row, Value digit) {
                for (int i = 0; i < grid.length; i++) {
                    if (grid[row][i].equals(digit)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean isNumberInCol(int col, Value digit) {
                for (Value[] values : grid) {
                    if (values[col].equals(digit)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean isNumberInQuad(int row, int col, Value digit) {
                int iRow = row - row % half;
                int iCol = col - col % half;
                for (int i = iRow; i < iRow + half; i++) {
                    for (int j = iCol; j < iCol + half; j++) {
                        if (grid[i][j].equals(digit)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void set(int row, int col, Value digit) {
                grid[row][col] = digit;
            }

            @Override
            public void print(String title) {
                if (!title.isEmpty()) {
                    System.out.println(title);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("n.  ");
                for (int col = 0; col < size(); col++) {
                    if (col % 3 == 0 && col != 0) {
                        sb.append("  ");
                    }
                    sb.append(col + 1);
                }
                sb.append(" \n");
                for (int row = 0; row < size(); row++) {
                    if (row > 0) {
                        sb.append("\n");
                    }
                    sb.append(String.format("%d. {", row + 1));
                    for (int col = 0; col < size(); col++) {
                        if (col % 3 == 0 && col != 0) {
                            sb.append("}{");
                        }
                        sb.append(grid[row][col]);
                    }
                    sb.append("}");
                }
                System.out.println(sb);
            }
        }
    }

    @SuppressWarnings("PMD.ConstantsInInterface")
    interface Value {
        Value EMPTY = new Empty();
        Value[] DIGITS = new Value[]{
            EMPTY, new Digit(1), new Digit(2), new Digit(3), new Digit(4),
            new Digit(5), new Digit(6), new Digit(7), new Digit(8), new Digit(9)
        };


        final class Empty implements Value {
            private Empty() {
            }

            @SuppressWarnings("PMD.SimplifyBooleanReturns")
            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                return o != null && getClass() == o.getClass();
            }

            @SuppressWarnings("PMD.UselessOverridingMethod")
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public String toString() {
                return "-";
            }
        }

        final class Digit implements Value {
            private final int digit;

            private Digit(int digit) {
                this.digit = digit;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Digit other = (Digit) o;
                return Objects.equals(digit, other.digit);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(digit);
            }

            @Override
            public String toString() {
                return "" + digit;
            }
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateField")
    final class RecursionFallback implements Sudoku {
        private final Block grid;
        private int deep = 0;
        private int iterations = 0;

        private RecursionFallback(int size, int[][] values) {
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
}
