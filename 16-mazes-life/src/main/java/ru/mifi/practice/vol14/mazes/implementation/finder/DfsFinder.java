package ru.mifi.practice.vol14.mazes.implementation.finder;

import ru.mifi.practice.vol14.mazes.Maze;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

//DFS
/** Поиск пути в лабиринте обходом в глубину — находит любой путь, не обязательно кратчайший. */
public final class DfsFinder implements Maze.Finder {
    private static final Color PATH_COLOR = Color.BLUE;
    private final Maze.Representation repr;
    private final Color pathColor;

    public DfsFinder() {
        this(null);
    }

    public DfsFinder(Maze.Representation repr) {
        this(repr, PATH_COLOR);
    }

    public DfsFinder(Maze.Representation repr, Color pathColor) {
        this.repr = repr;
        this.pathColor = pathColor;
    }

    @Override
    public Maze.Point[] findPath(Maze.Grid maze) {
        int rows = maze.rows();
        int cols = maze.cols();
        boolean[][] visited = new boolean[rows][cols];
        int[][] fromRow = new int[rows][cols];
        int[][] fromCol = new int[rows][cols];
        visited[0][0] = true;
        fromRow[0][0] = -1;
        fromCol[0][0] = -1;
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, 0});
        Frames frames = new Frames();
        while (!stack.isEmpty()) {
            int[] cell = stack.pop();
            int row = cell[0];
            int col = cell[1];
            if (row == rows - 1 && col == cols - 1) {
                return reconstruct(fromRow, fromCol, rows, cols);
            }
            for (Direction direction : Direction.NEIGHBOURS) {
                int nextRow = row + direction.deltaRow();
                int nextCol = col + direction.deltaCol();
                if (isOpen(maze, row, col, nextRow, nextCol, direction) && !visited[nextRow][nextCol]) {
                    visited[nextRow][nextCol] = true;
                    fromRow[nextRow][nextCol] = row;
                    fromCol[nextRow][nextCol] = col;
                    frames.record(maze, new Maze.Point(nextCol, nextRow));
                    stack.push(new int[]{nextRow, nextCol});
                }
            }
        }
        return new Maze.Point[0];
    }

    private boolean isOpen(Maze.Grid maze, int row, int col, int nextRow, int nextCol, Direction direction) {
        if (nextRow < 0 || nextRow >= maze.rows() || nextCol < 0 || nextCol >= maze.cols()) {
            return false;
        }
        return (maze.data(row, col) & direction.fromWall()) == 0
            && (maze.data(nextRow, nextCol) & direction.toWall()) == 0;
    }

    private Maze.Point[] reconstruct(int[][] fromRow, int[][] fromCol, int rows, int cols) {
        int row = rows - 1;
        int col = cols - 1;
        if (fromRow[row][col] == -1 && (row != 0 || col != 0)) {
            return new Maze.Point[0];
        }
        List<Maze.Point> path = new ArrayList<>();
        while (row != -1 && col != -1) {
            path.add(0, new Maze.Point(col, row));
            int previousRow = fromRow[row][col];
            int previousCol = fromCol[row][col];
            row = previousRow;
            col = previousCol;
        }
        return path.toArray(new Maze.Point[0]);
    }

    private record Direction(int deltaRow, int deltaCol, int fromWall, int toWall) {
        private static final Direction[] NEIGHBOURS = {
            new Direction(-1, 0, Maze.SQUARE_UP, Maze.SQUARE_DOWN),
            new Direction(1, 0, Maze.SQUARE_DOWN, Maze.SQUARE_UP),
            new Direction(0, -1, Maze.SQUARE_LEFT, Maze.SQUARE_RIGHT),
            new Direction(0, 1, Maze.SQUARE_RIGHT, Maze.SQUARE_LEFT),
        };
    }

    private final class Frames {
        private final List<Maze.Point> points = new ArrayList<>();
        private int index;

        void record(Maze.Grid maze, Maze.Point point) {
            if (repr == null) {
                return;
            }
            points.add(point);
            repr.snapshot(index, maze, points.toArray(new Maze.Point[0]), pathColor);
            index++;
        }
    }
}
