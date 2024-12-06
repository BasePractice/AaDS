package ru.mifi.practice.vol6.tree.paths;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Path;
import ru.mifi.practice.vol6.tree.Tree;

import java.util.Collections;
import java.util.List;

public final class MergePath<T> implements Path<T> {

    @Override
    public List<Node<T>> path(Tree<T> tree, T start, T end) {
        Node<T> it = tree.find(start);
        Node<T> nEnd = it.search(end);
        if (nEnd != null) {
            return it.path(end);
        }
        while (it != null) {
            List<Node<T>> path = it.path(end);
            if (!path.isEmpty()) {
                List<Node<T>> result = it.path(start);
                Collections.reverse(result);
                result.remove(result.size() - 1);
                result.addAll(path);
                return result;
            }
            it = it.parent();
        }
        return List.of();
    }
}
