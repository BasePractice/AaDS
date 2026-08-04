package ru.mifi.practice.vol6.tree.paths;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Path;
import ru.mifi.practice.vol6.tree.Tree;
import ru.mifi.practice.vol6.tree.VisitorStrategy;
import ru.mifi.practice.vol6.tree.visitors.Distance;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Путь между двумя значениями через их наименьшего общего предка. */
public final class LowestCommonAncestorPath<T> implements Path<T> {
    @Override
    public List<Node<T>> path(Tree<T> tree, T start, T end) {
        Distance<T> vDistance = new Distance<>();
        tree.visit(vDistance, new VisitorStrategy.PreOrder<>());
        Map<Node<T>, Integer> distances = vDistance.distances();
        var origin = tree.find(start);
        var originCursor = origin;
        var terminus = tree.find(end);
        var terminusCursor = terminus;
        int originHeight = distances.get(origin);
        int terminusHeight = distances.get(terminus);
        while (originHeight != terminusHeight) {
            if (originHeight > terminusHeight) {
                originCursor = originCursor.parent();
                originHeight -= 1;
            } else {
                terminusCursor = terminusCursor.parent();
                terminusHeight -= 1;
            }
        }
        while (!originCursor.equals(terminusCursor)) {
            originCursor = originCursor.parent();
            terminusCursor = terminusCursor.parent();
        }
        var lca = originCursor;
        List<Node<T>> path = lca.path(start);
        Collections.reverse(path);
        path.remove(path.size() - 1);
        path.addAll(lca.path(end));
        return path;
    }
}
