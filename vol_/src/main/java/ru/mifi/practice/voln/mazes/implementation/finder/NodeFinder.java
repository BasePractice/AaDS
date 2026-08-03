package ru.mifi.practice.voln.mazes.implementation.finder;

import ru.mifi.practice.voln.mazes.Maze;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/** Поиск кратчайшего пути в лабиринте обходом в ширину. */
public record NodeFinder(Maze.Representation repr, Color pathColor) implements Maze.Finder {
    private static final Color PATH_COLOR = Color.BLUE;

    public NodeFinder() {
        this(null);
    }

    public NodeFinder(Maze.Representation repr) {
        this(repr, PATH_COLOR);
    }

    @Override
    public Maze.Point[] findPath(Maze.Grid maze) {
        Bfs bfs = start(maze.rows(), maze.cols());
        Frames frames = new Frames();
        while (!bfs.queue().isEmpty()) {
            int[] cell = bfs.queue().poll();
            int row = cell[0];
            int col = cell[1];
            if (row == maze.rows() - 1 && col == maze.cols() - 1) {
                break;
            }
            for (Direction direction : Direction.NEIGHBOURS) {
                relax(maze, bfs, frames, row, col, direction);
            }
        }
        return reconstruct(bfs, maze.rows(), maze.cols());
    }

    private Bfs start(int rows, int cols) {
        int[][] dist = new int[rows][cols];
        for (int[] row : dist) {
            Arrays.fill(row, -1);
        }
        int[][] fromRow = new int[rows][cols];
        int[][] fromCol = new int[rows][cols];
        dist[0][0] = 0;
        fromRow[0][0] = -1;
        fromCol[0][0] = -1;
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0});
        return new Bfs(dist, fromRow, fromCol, queue);
    }

    private void relax(Maze.Grid maze, Bfs bfs, Frames frames, int row, int col, Direction direction) {
        int nextRow = row + direction.deltaRow();
        int nextCol = col + direction.deltaCol();
        if (!open(maze, row, col, direction) || bfs.dist()[nextRow][nextCol] != -1) {
            return;
        }
        bfs.dist()[nextRow][nextCol] = bfs.dist()[row][col] + 1;
        bfs.fromRow()[nextRow][nextCol] = row;
        bfs.fromCol()[nextRow][nextCol] = col;
        bfs.queue().add(new int[]{nextRow, nextCol});
        frames.record(this, maze, new Maze.Point(nextCol, nextRow));
    }

    private boolean open(Maze.Grid maze, int row, int col, Direction direction) {
        int nextRow = row + direction.deltaRow();
        int nextCol = col + direction.deltaCol();
        if (nextRow < 0 || nextRow >= maze.rows() || nextCol < 0 || nextCol >= maze.cols()) {
            return false;
        }
        return (maze.data(row, col) & direction.fromWall()) == 0
            && (maze.data(nextRow, nextCol) & direction.toWall()) == 0;
    }

    private Maze.Point[] reconstruct(Bfs bfs, int rows, int cols) {
        int row = rows - 1;
        int col = cols - 1;
        if (bfs.dist()[row][col] == -1) {
            return new Maze.Point[0];
        }
        List<Maze.Point> path = new ArrayList<>();
        while (row != -1 && col != -1) {
            path.add(0, new Maze.Point(col, row));
            int previousRow = bfs.fromRow()[row][col];
            int previousCol = bfs.fromCol()[row][col];
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

    private record Bfs(int[][] dist, int[][] fromRow, int[][] fromCol, Deque<int[]> queue) {
    }

    private static final class Frames {
        private final List<Maze.Point> points = new ArrayList<>();
        private int index;

        void record(NodeFinder owner, Maze.Grid maze, Maze.Point point) {
            if (owner.repr() == null) {
                return;
            }
            points.add(point);
            owner.repr().snapshot(index, maze, points.toArray(new Maze.Point[0]), owner.pathColor());
            index++;
        }
    }
}
