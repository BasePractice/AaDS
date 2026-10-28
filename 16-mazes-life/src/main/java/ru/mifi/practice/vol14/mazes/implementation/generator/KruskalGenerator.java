package ru.mifi.practice.vol14.mazes.implementation.generator;

import ru.mifi.practice.vol14.mazes.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//Kruskal
/** Генерация лабиринта алгоритмом Крускала через Union-Find (Disjoint Set). */
public final class KruskalGenerator implements Maze.Generator {

    @Override
    public Maze.Grid generate(int rows, int cols) {
        Cell[][] cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
            }
        }
        UnionFind unionFind = new UnionFind(rows * cols);
        List<Edge> edges = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r + 1 < rows) {
                    edges.add(new Edge(r, c, r + 1, c));
                }
                if (c + 1 < cols) {
                    edges.add(new Edge(r, c, r, c + 1));
                }
            }
        }
        Collections.shuffle(edges, ThreadLocalRandom.current());
        for (Edge edge : edges) {
            int from = index(cols, edge.fromRow(), edge.fromCol());
            int to = index(cols, edge.toRow(), edge.toCol());
            if (unionFind.find(from) != unionFind.find(to)) {
                removeWall(cells, edge.fromRow(), edge.fromCol(), edge.toRow(), edge.toCol());
                unionFind.union(from, to);
            }
        }
        Maze.Grid grid = new Maze.Grid(cols, rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid.set(r, c, cells[r][c].value());
            }
        }
        return grid;
    }

    private int index(int cols, int r, int c) {
        return r * cols + c;
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

    private record Edge(int fromRow, int fromCol, int toRow, int toCol) {
    }

    private static final class UnionFind {
        private final int[] parent;
        private final int[] rank;

        UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) {
                return;
            }
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }
}
