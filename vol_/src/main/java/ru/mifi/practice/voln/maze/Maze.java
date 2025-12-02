package ru.mifi.practice.voln.maze;

import java.util.Objects;

@SuppressWarnings("PMD.ConstantsInInterface")
public interface Maze {
    char SQUARE_LEFT = 1;
    char SQUARE_UP = 2;
    char SQUARE_RIGHT = 4;
    char SQUARE_DOWN = 8;

    @FunctionalInterface
    interface Finder {
        Point[] findPath(Grid grid);
    }

    @FunctionalInterface
    interface Generator {
        Grid generate(int rows, int cols);
    }

    @FunctionalInterface
    interface Serializer {
        String serialize(Grid grid);
    }

    @FunctionalInterface
    interface Representation {
        void representation(String name, Grid grid, Point[] path);
    }

    record Grid(int cols, int rows, char[][] data) {
        public Grid(int cols, int rows) {
            this(cols, rows, new char[rows][cols]);
        }

        public Grid {
            assert cols > 0;
            assert rows > 0;
            assert data.length == cols;
            assert data[0].length == rows;
        }

        public char data(int row, int col) {
            Objects.checkIndex(row, rows);
            Objects.checkIndex(col, cols);
            return data[row][col];
        }

        public void set(int row, int col, char value) {
            Objects.checkIndex(row, rows);
            Objects.checkIndex(col, cols);
            data[row][col] = value;
        }
    }

    record Point(int x, int y) {
    }
}
