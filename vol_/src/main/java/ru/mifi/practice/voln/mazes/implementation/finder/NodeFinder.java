package ru.mifi.practice.voln.mazes.implementation.finder;

import ru.mifi.practice.voln.mazes.Maze;
import ru.mifi.practice.voln.mazes.implementation.NodeCommon;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class NodeFinder extends NodeCommon implements Maze.Finder {
    private final Maze.Representation repr;

    public NodeFinder(Maze.Representation representation) {
        this.repr = representation;
    }

    public NodeFinder() {
        this(null);
    }

    private static Node getNearest(List<Node> neighbors) {
        neighbors.sort((o1, o2) -> {
            Integer d1 = o1.distance;
            Integer d2 = o2.distance;
            return d1.compareTo(d2);
        });
        for (Node node : neighbors) {
            if (node.distance != -1) {
                return node;
            }
        }
        return null;
    }

    @Override
    public Maze.Point[] findPath(Maze.Grid maze) {
        Node[][] nodes = new Node[maze.rows()][maze.cols()];
        Set<Node> nodeSet = new HashSet<>();
        for (int y = 0; y < maze.rows(); y++) {
            for (int x = 0; x < maze.cols(); x++) {
                Node node = new Node(x, y, maze.data(x, y));
                nodes[x][y] = node;
                nodeSet.add(node);
            }
        }

        Node start = nodes[0][0];
        start.distance = 0;
        Node finish = nodes[maze.rows() - 1][maze.cols() - 1];
        int wave = 0;
        int index = 0;
        List<Maze.Point> points = new ArrayList<>();
        do {
            for (Node node : nodeSet) {
                if (node.distance == wave) {
                    List<Node> neighbors = node.neighbors(nodeSet);
                    for (Node neighbor : neighbors) {
                        if (neighbor.distance == -1) {
                            points.add(new Maze.Point(neighbor.x, neighbor.y));
                            neighbor.distance = wave + 1;
                            if (repr != null) {
                                repr.snapshot(index, maze, points.toArray(new Maze.Point[0]), Color.ORANGE);
                                ++index;
                            }
                        }
                    }
                }
            }
            wave++;
        } while (finish.distance == -1);
        List<Node> path = new ArrayList<>();
        path.add(finish);
        Node node = finish;
        while (!path.contains(start) && node != null) {
            node = getNearest(node.neighbors(nodeSet));
            path.add(node);
        }
        return path.stream().map(p -> new Maze.Point(p.x, p.y)).toArray(Maze.Point[]::new);
    }
}
