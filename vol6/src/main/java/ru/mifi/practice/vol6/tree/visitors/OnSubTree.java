package ru.mifi.practice.vol6.tree.visitors;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class OnSubTree<T> implements Visitor<T> {
    private final Map<Node<T>, Integer> stepsIn = new HashMap<>();
    private final Map<Node<T>, Integer> stepsOut = new HashMap<>();
    private final AtomicInteger steps = new AtomicInteger();

    void clear() {
        stepsIn.clear();
        stepsOut.clear();
        steps.set(0);
    }

    @Override
    public void enterNode(Node<T> node) {
        if (node != null) {
            int step = steps.incrementAndGet();
            stepsIn.put(node, step);
        }
    }

    @Override
    public void exitNode(Node<T> node) {
        if (node != null) {
            stepsOut.put(node, steps.incrementAndGet());
        }
    }

    public int in(Node<T> node) {
        return stepsIn.get(node);
    }

    public int out(Node<T> node) {
        return stepsOut.get(node);
    }

    @SuppressWarnings("checked")
    public List<Node<T>> times() {
        List<Node<T>> result = new ArrayList<>();
        Node[] nodes = new Node[stepsIn.size() * 2 + 1];
        for (var key : stepsIn.keySet()) {
            int in = stepsIn.get(key);
            int out = stepsOut.get(key);
            nodes[in] = key;
            nodes[out] = key;
        }
        for (Node node : nodes) {
            if (node == null) {
                continue;
            }
            result.add(node);
        }
        return result;
    }


    @Override
    public void empty() {
        //None
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder fl = new StringBuilder();
        StringBuilder sl = new StringBuilder();
        List<Node<T>> times = times();
        for (int i = 0; i < times.size(); i++) {
            Node<T> node = times.get(i);
            fl.append(String.format("%2d ", i));
            sl.append(String.format("%2s ", node.value()));
        }
        sb.append(fl).append("\n").append(sl);
        return sb.toString();
    }
}
