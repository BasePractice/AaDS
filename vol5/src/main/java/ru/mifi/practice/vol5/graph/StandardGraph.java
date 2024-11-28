package ru.mifi.practice.vol5.graph;

import java.util.concurrent.atomic.AtomicInteger;

public final class StandardGraph<T, W extends Number & Comparable<W>> extends Graph.AbstractGraph<T, W> {
    private final AtomicInteger vertexCount = new AtomicInteger(0);

    @Override
    protected String nextVertexId() {
        return String.valueOf(vertexCount.incrementAndGet());
    }
}
