package ru.mifi.practice.voln.mazes.implementation.generator;

import ru.mifi.practice.voln.mazes.Maze;
import ru.mifi.practice.voln.mazes.implementation.NodeCommon;

import java.util.Date;
import java.util.Random;

public final class NodeGenerator extends NodeCommon implements Maze.Generator {
    private final Random random = new Random(new Date().getTime());

    @Override
    public Maze.Grid generate(int rows, int cols) {
        Node[][] nodes = new Node[rows][cols];
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                Node nc = new Node(i, j);
                if (j == 0) {
                    nc.right = false;
                    nc.left = false;
                    nc.up = true;
                } else {
                    nc.up = true;
                    nc.down = true;
                    nc.right = true;
                    if (random.nextBoolean() || i == 0) {
                        nc.up = false;
                        nc.left = true;
                        nodes[i][j - 1].down = false;
                    } else {
                        nc.left = false;
                        nodes[i - 1][j].right = false;
                    }
                }

                if (j == rows - 1) {
                    nc.down = true;
                }

                if (i == cols - 1) {
                    nc.right = true;
                }
                nodes[i][j] = nc;
            }
        }

        Maze.Grid grid = new Maze.Grid(rows, cols);
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                grid.set(x, y, nodes[x][y].value());
            }
        }
        return grid;
    }
}
