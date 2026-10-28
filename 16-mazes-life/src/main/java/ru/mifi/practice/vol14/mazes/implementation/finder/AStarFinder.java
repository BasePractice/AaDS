package ru.mifi.practice.vol14.mazes.implementation.finder;

import ru.mifi.practice.vol14.mazes.Maze;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

//A*
/** Поиск пути в лабиринте алгоритмом A* с эвристикой Manhattan distance. */
public final class AStarFinder implements Maze.Finder {
    private static final Color PATH_COLOR = Color.BLUE;
    private final Maze.Representation repr;
    private final Color pathColor;

    public AStarFinder() {
        this(null);
    }

    public AStarFinder(Maze.Representation repr) {
        this(repr, PATH_COLOR);
    }

    public AStarFinder(Maze.Representation repr, Color pathColor) {
        this.repr = repr;
        this.pathColor = pathColor;
    }

    @Override
    public Maze.Point[] findPath(Maze.Grid maze) {
        int rows = maze.rows();
        int cols = maze.cols();
        int[][] gScore = new int[rows][cols];
        for (int[] row : gScore) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        int[][] fromRow = new int[rows][cols];
        int[][] fromCol = new int[rows][cols];
        gScore[0][0] = 0;
        fromRow[0][0] = -1;
        fromCol[0][0] = -1;
        Queue<Node> openSet = new PriorityQueue<>();
        openSet.add(new Node(0, 0, heuristic(0, 0, rows, cols)));
        boolean[][] closed = new boolean[rows][cols];
        Frames frames = new Frames();
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            int row = current.row();
            int col = current.col();
            if (row == rows - 1 && col == cols - 1) {
                return reconstruct(fromRow, fromCol, rows, cols);
            }
            if (closed[row][col]) {
                continue;
            }
            closed[row][col] = true;
            for (Direction direction : Direction.NEIGHBOURS) {
                int nextRow = row + direction.deltaRow();
                int nextCol = col + direction.deltaCol();
                if (!isOpen(maze, row, col, nextRow, nextCol, direction)) {
                    continue;
                }
                int tentativeG = gScore[row][col] + 1;
                if (tentativeG < gScore[nextRow][nextCol]) {
                    gScore[nextRow][nextCol] = tentativeG;
                    fromRow[nextRow][nextCol] = row;
                    fromCol[nextRow][nextCol] = col;
                    frames.record(maze, new Maze.Point(nextCol, nextRow));
                    openSet.add(new Node(nextRow, nextCol, tentativeG + heuristic(nextRow, nextCol, rows, cols)));
                }
            }
        }
        return new Maze.Point[0];
    }

    private int heuristic(int row, int col, int rows, int cols) {
        return Math.abs(rows - 1 - row) + Math.abs(cols - 1 - col);
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

    private record Node(int row, int col, int f) implements Comparable<Node> {
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f(), other.f());
        }
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
