package ru.mifi.practice.vol2.sudoku;

public sealed interface Block {
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
