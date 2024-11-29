package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.List;

public final class AntShortestPath<T, W extends Number & Comparable<W>> implements Algorithms.ShortestPath<T, W> {
    @Override
    public List<String> shortestPath(Graph<T, W> graph, String source, String target) {
        return List.of();
    }
}
