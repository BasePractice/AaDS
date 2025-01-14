package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.List;
import java.util.Map;

public interface Algorithms {

    @FunctionalInterface
    interface Visitor<T, W extends Number & Comparable<W>> {
        void visit(Graph.Vertex<T, W> vertex);
    }

    interface FirstSearch<T, W extends Number & Comparable<W>> {
        void dfs(Graph<T, W> graph, Visitor<T, W> visitor);

        void bfs(Graph<T, W> graph, Visitor<T, W> visitor);
    }

    interface CircleSearch<T, W extends Number & Comparable<W>> {
        List<Graph.Vertex<T, W>> searchCircle(Graph<T, W> graph);
    }

    @FunctionalInterface
    interface ShortestDistance<T, W extends Number & Comparable<W>> {
        Map<Graph.Vertex<T, W>, W> distances(Graph<T, W> graph, Graph.Vertex<T, W> source);
    }

    @FunctionalInterface
    interface ShortestPath<T, W extends Number & Comparable<W>> {
        List<Graph.Vertex<T, W>> shortestPath(Graph<T, W> graph, Graph.Vertex<T, W> source, Graph.Vertex<T, W> target);
    }
}
