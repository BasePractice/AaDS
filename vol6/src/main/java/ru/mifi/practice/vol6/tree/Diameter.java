package ru.mifi.practice.vol6.tree;

import ru.mifi.practice.vol6.tree.paths.LowestCommonAncestorPath;
import ru.mifi.practice.vol6.tree.visitors.Distance;

import java.util.List;

public final class Diameter<T> {

    public List<Node<T>> path(Tree<T> tree) {
        VisitorStrategy.PreOrder<T> strategy = new VisitorStrategy.PreOrder<>();
        Distance<T> vDistance = new Distance<>();
        tree.visit(vDistance, strategy);
        int max = 0;
        Node<T> maxNode = null;
        for (var entry : vDistance.distances().entrySet()) {
            if (max < entry.getValue()) {
                max = entry.getValue();
                maxNode = entry.getKey();
            }
        }
        if (maxNode != null) {
            VisitorStrategy.AlreadyVisited<T> alreadyVisited = new VisitorStrategy.AlreadyVisited<>(strategy);
            final Node<T> start = maxNode;
            Node<T> it = maxNode;
            int distance = 0;
            vDistance.clear();
            while (it != null) {
                vDistance.resetLevel(distance);
                it.visit(vDistance, alreadyVisited);
                ++distance;
                it = it.parent();
            }
            max = 0;
            for (var entry : vDistance.distances().entrySet()) {
                if (max < entry.getValue()) {
                    max = entry.getValue();
                    maxNode = entry.getKey();
                }
            }
            Node<T> end = maxNode;
            return new LowestCommonAncestorPath<T>().path(tree, start.value(), end.value());
        }
        return List.of();
    }
}
