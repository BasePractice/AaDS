package ru.mifi.practice.voln.mazes.implementation.finder;

import ru.mifi.practice.voln.mazes.Maze;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

//BFS
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
        final int rows = maze.rows();
        final int cols = maze.cols();

        int[][] dist = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                dist[r][c] = -1;
            }
        }
        int[][] pr = new int[rows][cols];
        int[][] pc = new int[rows][cols];

        final Deque<int[]> q = new ArrayDeque<>();
        dist[0][0] = 0;
        pr[0][0] = -1;
        pc[0][0] = -1;
        q.add(new int[]{0, 0}); // (row, col)

        int snapshotIndex = 0;
        List<Maze.Point> discoveredPoints = repr != null ? new ArrayList<>() : null;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0];
            int c = cur[1];
            if (r == rows - 1 && c == cols - 1) {
                break;
            }

            char data = maze.data(r, c);
            // Up
            if (r > 0 && (data & Maze.SQUARE_UP) == 0 && (maze.data(r - 1, c) & Maze.SQUARE_DOWN) == 0 && dist[r - 1][c] == -1) {
                dist[r - 1][c] = dist[r][c] + 1;
                pr[r - 1][c] = r;
                pc[r - 1][c] = c;
                q.add(new int[]{r - 1, c});
                if (repr != null) {
                    discoveredPoints.add(new Maze.Point(c, r - 1));
                    repr.snapshot(snapshotIndex, maze, discoveredPoints.toArray(new Maze.Point[0]), pathColor);
                    snapshotIndex++;
                }
            }
            // Down
            if (r + 1 < rows && (data & Maze.SQUARE_DOWN) == 0 && (maze.data(r + 1, c) & Maze.SQUARE_UP) == 0 && dist[r + 1][c] == -1) {
                dist[r + 1][c] = dist[r][c] + 1;
                pr[r + 1][c] = r;
                pc[r + 1][c] = c;
                q.add(new int[]{r + 1, c});
                if (repr != null) {
                    discoveredPoints.add(new Maze.Point(c, r + 1));
                    repr.snapshot(snapshotIndex, maze, discoveredPoints.toArray(new Maze.Point[0]), pathColor);
                    snapshotIndex++;
                }
            }
            // Left
            if (c > 0 && (data & Maze.SQUARE_LEFT) == 0 && (maze.data(r, c - 1) & Maze.SQUARE_RIGHT) == 0 && dist[r][c - 1] == -1) {
                dist[r][c - 1] = dist[r][c] + 1;
                pr[r][c - 1] = r;
                pc[r][c - 1] = c;
                q.add(new int[]{r, c - 1});
                if (repr != null) {
                    discoveredPoints.add(new Maze.Point(c - 1, r));
                    repr.snapshot(snapshotIndex, maze, discoveredPoints.toArray(new Maze.Point[0]), pathColor);
                    snapshotIndex++;
                }
            }
            // Right
            if (c + 1 < cols && (data & Maze.SQUARE_RIGHT) == 0 && (maze.data(r, c + 1) & Maze.SQUARE_LEFT) == 0 && dist[r][c + 1] == -1) {
                dist[r][c + 1] = dist[r][c] + 1;
                pr[r][c + 1] = r;
                pc[r][c + 1] = c;
                q.add(new int[]{r, c + 1});
                if (repr != null) {
                    discoveredPoints.add(new Maze.Point(c + 1, r));
                    repr.snapshot(snapshotIndex, maze, discoveredPoints.toArray(new Maze.Point[0]), pathColor);
                    snapshotIndex++;
                }
            }
        }

        List<Maze.Point> path = new ArrayList<>();
        int r = rows - 1;
        int c = cols - 1;
        if (dist[r][c] == -1) {
            return new Maze.Point[0];
        }
        while (r != -1 && c != -1) {
            path.add(0, new Maze.Point(c, r));
            int tr = pr[r][c];
            int tc = pc[r][c];
            r = tr;
            c = tc;
        }
        return path.toArray(new Maze.Point[0]);
    }
}
