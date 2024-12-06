package ru.mifi.practice.vol6.tree.paths;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Path;
import ru.mifi.practice.vol6.tree.Tree;
import ru.mifi.practice.vol6.tree.VisitorStrategy;
import ru.mifi.practice.vol6.tree.visitors.Distance;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LowestCommonAncestorPath<T> implements Path<T> {
    @Override
    public List<Node<T>> path(Tree<T> tree, T start, T end) {
        Distance<T> vDistance = new Distance<>();
        tree.visit(vDistance, new VisitorStrategy.PreOrder<>());
        Map<Node<T>, Integer> distances = vDistance.distances();
        var nStart = tree.find(start);
        var nStartIt = nStart;
        var nEnd = tree.find(end);
        var nEndIt = nEnd;
        int hStart = distances.get(nStart);
        int hEnd = distances.get(nEnd);
        while (hStart != hEnd) {
            if (hStart > hEnd) {
                nStartIt = nStartIt.parent();
                hStart -= 1;
            } else {
                nEndIt = nEndIt.parent();
                hEnd -= 1;
            }
        }
        while (!nStartIt.equals(nEndIt)) {
            nStartIt = nStartIt.parent();
            nEndIt = nEndIt.parent();
        }
        var lca = nStartIt;
        List<Node<T>> path = lca.path(start);
        Collections.reverse(path);
        path.remove(path.size() - 1);
        path.addAll(lca.path(end));
        return path;
    }
}
