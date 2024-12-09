package ru.mifi.practice.vol6.tree;

import ru.mifi.practice.vol6.tree.visitors.Count;

@SuppressWarnings({"PMD.SingularField", "PMD.UnusedPrivateMethod"})
public final class Centroid<T> implements Visitor<T> {
    private Node<T> centroid = null;
    private int vertexes = 0;

    private static <T> int nodeCount(Node<T> node, Count<T> count, VisitorStrategy.PreOrder<T> strategy) {
        count.clear();
        if (node == null) {
            return 0;
        }
        node.visit(count, strategy);
        return count.count();
    }

    private static <T> int nodeUpper(Node<T> node, Count<T> count, VisitorStrategy.PreOrder<T> strategy) {
        count.clear();
        VisitorStrategy.AlreadyVisited<T> alreadyVisited = new VisitorStrategy.AlreadyVisited<>(strategy);
        alreadyVisited.put(node);
        Node<T> it = node.parent();
        while (it != null) {
            it.visit(count, alreadyVisited);
            it = it.parent();
        }
        return count.count();
    }

    public Node<T> centroid(Tree<T> tree) {
        centroid = null;
        VisitorStrategy.PreOrder<T> strategy = new VisitorStrategy.PreOrder<>();
        Count<T> count = new Count<>();
        tree.visit(count, strategy);
        vertexes = count.count();
        tree.visit(this, strategy);
        return centroid;
    }

    @Override
    public void enterNode(Node<T> node) {
        VisitorStrategy.PreOrder<T> strategy = new VisitorStrategy.PreOrder<>();
        Count<T> count = new Count<>();
        var upper = nodeUpper(node, count, strategy);
        var left = nodeCount(node.left(), count, strategy);
        var right = nodeCount(node.right(), count, strategy);
        if (left <= vertexes / 2 && right <= vertexes / 2 && upper <= vertexes / 2) {
            centroid = node;
        }
    }

    @Override
    public void exitNode(Node<T> node) {
        //None
    }

    @Override
    public void empty() {
        //None
    }
}
