package ru.mifi.practice.vol6.tree.visitors;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Visitor;

import java.util.ArrayList;
import java.util.List;

public final class EulerPath<T> implements Visitor<T> {
    private final List<Node<T>> nodes = new ArrayList<>();

    void clear() {
        nodes.clear();
    }

    @Override
    public void enterNode(Node<T> node) {
        if (node != null) {
            nodes.add(node);
        }
    }

    @Override
    public void exitNode(Node<T> node) {
        if (node != null) {
            nodes.add(node);
        }
    }

    @SuppressWarnings("checked")
    public List<Node<T>> path() {
        return nodes;
    }


    @Override
    public void empty() {
        //None
    }

    @Override
    public String toString() {
        return String.valueOf(nodes);
    }
}
