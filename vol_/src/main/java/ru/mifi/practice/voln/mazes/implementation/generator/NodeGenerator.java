package ru.mifi.practice.voln.mazes.implementation.generator;

import ru.mifi.practice.voln.mazes.Maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//DFS
public final class NodeGenerator implements Maze.Generator {

    @Override
    public Maze.Grid generate(int rows, int cols) {
        Node[][] nodes = new Node[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                nodes[r][c] = new Node();
            }
        }

        boolean[][] visited = new boolean[rows][cols];
        Deque<int[]> stack = new ArrayDeque<>();
        visited[0][0] = true;
        stack.push(new int[]{0, 0});

        while (!stack.isEmpty()) {
            int[] cur = stack.peek();
            int r = cur[0];
            int c = cur[1];

            List<int[]> neighbors = new ArrayList<>(4);
            if (r > 0 && !visited[r - 1][c]) {
                neighbors.add(new int[]{r - 1, c});
            }
            if (r + 1 < rows && !visited[r + 1][c]) {
                neighbors.add(new int[]{r + 1, c});
            }
            if (c > 0 && !visited[r][c - 1]) {
                neighbors.add(new int[]{r, c - 1});
            }
            if (c + 1 < cols && !visited[r][c + 1]) {
                neighbors.add(new int[]{r, c + 1});
            }

            if (neighbors.isEmpty()) {
                stack.pop();
                continue;
            }

            int idx = ThreadLocalRandom.current().nextInt(neighbors.size());
            int[] nxt = neighbors.get(idx);
            int nr = nxt[0];
            int nc = nxt[1];

            if (nr == r - 1) { // up
                nodes[r][c].up = false;
                nodes[nr][nc].down = false;
            } else if (nr == r + 1) {
                nodes[r][c].down = false;
                nodes[nr][nc].up = false;
            } else if (nc == c - 1) {
                nodes[r][c].left = false;
                nodes[nr][nc].right = false;
            } else if (nc == c + 1) {
                nodes[r][c].right = false;
                nodes[nr][nc].left = false;
            }

            visited[nr][nc] = true;
            stack.push(new int[]{nr, nc});
        }

        Maze.Grid grid = new Maze.Grid(cols, rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid.set(r, c, nodes[r][c].value());
            }
        }
        return grid;
    }

    private static final class Node {

        private boolean up = true;
        private boolean down = true;
        private boolean left = true;
        private boolean right = true;

        private char value() {
            char val = 0;
            if (up) {
                val |= Maze.SQUARE_UP;
            }
            if (left) {
                val |= Maze.SQUARE_LEFT;
            }
            if (down) {
                val |= Maze.SQUARE_DOWN;
            }
            if (right) {
                val |= Maze.SQUARE_RIGHT;
            }
            return val;
        }
    }
}
