package ru.mifi.practice.vol14.mazes.implementation.generator;

import ru.mifi.practice.vol14.mazes.Maze;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//Prim
/** Генерация лабиринта алгоритмом Прима через frontier-список рёбер. */
public final class PrimGenerator implements Maze.Generator {

    @Override
    public Maze.Grid generate(int rows, int cols) {
        Cell[][] cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
            }
        }
        boolean[][] visited = new boolean[rows][cols];
        List<Edge> frontier = new ArrayList<>();
        visited[0][0] = true;
        addEdges(frontier, visited, 0, 0, rows, cols);
        while (!frontier.isEmpty()) {
            int index = ThreadLocalRandom.current().nextInt(frontier.size());
            Edge edge = frontier.remove(index);
            int nr = edge.row();
            int nc = edge.col();
            if (visited[nr][nc]) {
                continue;
            }
            visited[nr][nc] = true;
            int fr = edge.fromRow();
            int fc = edge.fromCol();
            removeWall(cells, fr, fc, nr, nc);
            addEdges(frontier, visited, nr, nc, rows, cols);
        }
        Maze.Grid grid = new Maze.Grid(cols, rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid.set(r, c, cells[r][c].value());
            }
        }
        return grid;
    }

    private void addEdges(List<Edge> frontier, boolean[][] visited, int row, int col, int rows, int cols) {
        if (row > 0 && !visited[row - 1][col]) {
            frontier.add(new Edge(row - 1, col, row, col));
        }
        if (row + 1 < rows && !visited[row + 1][col]) {
            frontier.add(new Edge(row + 1, col, row, col));
        }
        if (col > 0 && !visited[row][col - 1]) {
            frontier.add(new Edge(row, col - 1, row, col));
        }
        if (col + 1 < cols && !visited[row][col + 1]) {
            frontier.add(new Edge(row, col + 1, row, col));
        }
    }

    private void removeWall(Cell[][] cells, int fr, int fc, int tr, int tc) {
        if (tr == fr - 1) {
            cells[fr][fc].up = false;
            cells[tr][tc].down = false;
        } else if (tr == fr + 1) {
            cells[fr][fc].down = false;
            cells[tr][tc].up = false;
        } else if (tc == fc - 1) {
            cells[fr][fc].left = false;
            cells[tr][tc].right = false;
        } else if (tc == fc + 1) {
            cells[fr][fc].right = false;
            cells[tr][tc].left = false;
        }
    }

    private static final class Cell {
        private boolean up = true;
        private boolean down = true;
        private boolean left = true;
        private boolean right = true;

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

    private record Edge(int row, int col, int fromRow, int fromCol) {
    }
}
