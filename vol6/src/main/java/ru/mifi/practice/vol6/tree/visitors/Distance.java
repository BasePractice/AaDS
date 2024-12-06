package ru.mifi.practice.vol6.tree.visitors;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Distance<T> implements Visitor<T> {
    private final AtomicInteger level = new AtomicInteger(0);
    private final Map<Node<T>, Integer> distances = new HashMap<>();

    public void resetLevel(int level) {
        this.level.set(level);
    }

    public void clear() {
        distances.clear();
        resetLevel(0);
    }

    public Map<Node<T>, Integer> distances() {
        return distances;
    }

    @Override
    public void enterNode(Node<T> node) {
        int incremented = level.getAndIncrement();
        distances.put(node, incremented);
    }

    @Override
    public void exitNode(Node<T> node) {
        level.decrementAndGet();
    }

    @Override
    public void empty() {
        //None
    }
}
