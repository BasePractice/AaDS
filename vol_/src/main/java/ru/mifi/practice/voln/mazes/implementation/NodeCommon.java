package ru.mifi.practice.voln.mazes.implementation;

import ru.mifi.practice.voln.mazes.Maze;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.AbstractClassWithoutAnyMethod")
public abstract class NodeCommon {
    protected static final class Node {
        public final int x;
        public final int y;

        public boolean up;
        public boolean down;
        public boolean left;
        public boolean right;
        public int distance = -1;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Node(int x, int y, char c) {
            this.x = x;
            this.y = y;
            if ((c & Maze.SQUARE_LEFT) == Maze.SQUARE_LEFT) {
                this.left = true;
            }
            if ((c & Maze.SQUARE_UP) == Maze.SQUARE_UP) {
                this.up = true;
            }
            if ((c & Maze.SQUARE_RIGHT) == Maze.SQUARE_RIGHT) {
                this.right = true;
            }
            if ((c & Maze.SQUARE_DOWN) == Maze.SQUARE_DOWN) {
                this.down = true;
            }
        }

        public char value() {
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

        private boolean isNeighbor(Node node) {
            return Math.abs(this.x - node.x) + Math.abs(this.y - node.y) == 1;
        }

        private boolean canMoveTo(Node to) {
            if (this.isNeighbor(to)) {
                switch (this.x - to.x) {
                    case 0: {
                        switch (this.y - to.y) {
                            case 1: {
                                if (!this.up && !to.down) {
                                    return true;
                                }
                                break;
                            }
                            case -1: {
                                if (!this.down && !to.up) {
                                    return true;
                                }
                                break;
                            }
                            default: {
                                return false;
                            }
                        }
                        break;
                    }
                    case 1: {
                        if (!this.left && !to.right) {
                            return true;
                        }
                        break;
                    }
                    case -1: {
                        if (!this.right && !to.left) {
                            return true;
                        }
                        break;
                    }
                    default: {
                        return false;
                    }

                }
            }
            return false;
        }

        public List<Node> neighbors(Set<Node> nodes) {
            return nodes.stream().filter(node ->
                this.isNeighbor(node) &&
                    this.canMoveTo(node)).collect(Collectors.toList());
        }
    }
}
