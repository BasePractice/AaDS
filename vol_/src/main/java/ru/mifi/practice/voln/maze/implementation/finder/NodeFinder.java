package ru.mifi.practice.voln.maze.implementation.finder;

import ru.mifi.practice.voln.maze.Maze;
import ru.mifi.practice.voln.maze.implementation.NodeCommon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class NodeFinder extends NodeCommon implements Maze.Finder {
    private static Node getClosed(List<Node> neighbors) {
        neighbors.sort((o1, o2) -> {
            Integer d1 = o1.distance;
            Integer d2 = o2.distance;
            return d1.compareTo(d2);
        });
        for (Node cell : neighbors) {
            if (cell.distance != -1) {
                return cell;
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
                Node cell = new Node(x, y, maze.data(x, y));
                nodes[x][y] = cell;
                nodeSet.add(cell);
            }
        }

        Node start = nodes[0][0];
        start.distance = 0;
        Node finish = nodes[maze.rows() - 1][maze.cols() - 1];
        int wave = 0;
        do {
            for (Node node : nodeSet) {
                if (node.distance == wave) {
                    List<Node> neighbors = node.neighbors(nodeSet);
                    for (Node neighbor : neighbors) {
                        if (neighbor.distance == -1) {
                            neighbor.distance = wave + 1;
                        }
                    }
                }
            }
            wave++;
        } while (finish.distance == -1);
        List<Node> path = new ArrayList<>();
        path.add(finish);
        Node node = finish;
        while (!path.contains(start)) {
            assert node != null;
            node = getClosed(node.neighbors(nodeSet));
            path.add(node);
        }

        Maze.Point[] pathPoints = new Maze.Point[path.size()];
        for (int i = 0; i < pathPoints.length; i++) {
            pathPoints[i] = new Maze.Point(path.get(i).row, path.get(i).col);
        }

        return pathPoints;

    }
}
