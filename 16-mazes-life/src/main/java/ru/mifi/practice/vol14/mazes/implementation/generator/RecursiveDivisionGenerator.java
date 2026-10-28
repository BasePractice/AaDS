package ru.mifi.practice.vol14.mazes.implementation.generator;

import ru.mifi.practice.vol14.mazes.Maze;

import java.util.concurrent.ThreadLocalRandom;

//Recursive Division
/** Генерация лабиринта рекурсивным делением пространства стенами с проходами. */
public final class RecursiveDivisionGenerator implements Maze.Generator {

    @Override
    public Maze.Grid generate(int rows, int cols) {
        Cell[][] cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
            }
        }
        carve(cells, 0, 0, rows, cols);
        Maze.Grid grid = new Maze.Grid(cols, rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid.set(r, c, cells[r][c].value());
            }
        }
        return grid;
    }

    private void carve(Cell[][] cells, int rStart, int cStart, int height, int width) {
        if (height <= 1 || width <= 1) {
            return;
        }
        boolean horizontal;
        if (height == 2) {
            horizontal = false;
        } else if (width == 2) {
            horizontal = true;
        } else {
            horizontal = ThreadLocalRandom.current().nextBoolean();
        }
        if (horizontal) {
            int wallRow = rStart + ThreadLocalRandom.current().nextInt(1, height);
            int passage = cStart + ThreadLocalRandom.current().nextInt(0, width);
            for (int c = cStart; c < cStart + width; c++) {
                if (c != passage) {
                    cells[wallRow - 1][c].down = true;
                    cells[wallRow][c].up = true;
                }
            }
            carve(cells, rStart, cStart, wallRow - rStart, width);
            carve(cells, wallRow, cStart, rStart + height - wallRow, width);
        } else {
            int wallCol = cStart + ThreadLocalRandom.current().nextInt(1, width);
            int passage = rStart + ThreadLocalRandom.current().nextInt(0, height);
            for (int r = rStart; r < rStart + height; r++) {
                if (r != passage) {
                    cells[r][wallCol - 1].right = true;
                    cells[r][wallCol].left = true;
                }
            }
            carve(cells, rStart, cStart, height, wallCol - cStart);
            carve(cells, rStart, wallCol, height, cStart + width - wallCol);
        }
    }

    private static final class Cell {
        private boolean up;
        private boolean down;
        private boolean left;
        private boolean right;

        private char value() {
            char mask = 0;
            if (up) {
                mask |= Maze.SQUARE_UP;
            }
            if (left) {
                mask |= Maze.SQUARE_LEFT;
            }
            if (down) {
                mask |= Maze.SQUARE_DOWN;
            }
            if (right) {
                mask |= Maze.SQUARE_RIGHT;
            }
            return mask;
        }
    }
}
