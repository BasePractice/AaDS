package ru.mifi.practice.vol6.tree.visitors;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Visitor;

import java.util.ArrayList;
import java.util.List;

public final class Sequencer<T> implements Visitor<T> {
    private final List<Visitor<T>> visitors = new ArrayList<>();

    public void register(Visitor<T> visitor) {
        visitors.add(visitor);
    }

    public void unregister(Visitor<T> visitor) {
        visitors.remove(visitor);
    }

    @Override
    public void enterNode(Node<T> node) {
        for (Visitor<T> visitor : visitors) {
            visitor.enterNode(node);
        }
    }

    @Override
    public void exitNode(Node<T> node) {
        for (Visitor<T> visitor : visitors) {
            visitor.exitNode(node);
        }
    }

    @Override
    public void empty() {

    }
}
