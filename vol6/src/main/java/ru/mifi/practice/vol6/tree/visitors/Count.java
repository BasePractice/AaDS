package ru.mifi.practice.vol6.tree.visitors;

import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.Visitor;

import java.util.concurrent.atomic.AtomicInteger;

public final class Count<T> implements Visitor<T> {
    private final AtomicInteger count = new AtomicInteger(0);

    public void clear() {
        count.set(0);
    }

    public int count() {
        return count.get();
    }

    @Override
    public void enterNode(Node<T> node) {
        count.incrementAndGet();
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
