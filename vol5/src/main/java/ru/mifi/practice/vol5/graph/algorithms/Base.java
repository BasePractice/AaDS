package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Base<T, W extends Number & Comparable<W>>
    implements Algorithms.CircleSearch<T, W>, Algorithms.FirstSearch<T, W> {

    @Override
    public void dfs(Graph<T, W> graph, Algorithms.Visitor<T, W> visitor) {
        if (graph.size() > 0) {
            Set<Graph.Vertex<T, W>> visited = new HashSet<>();
            dfs(graph, graph.ofIndex(0), visitor, visited);
        }
    }

    private void dfs(Graph<T, W> graph, Graph.Vertex<T, W> source, Algorithms.Visitor<T, W> visitor, Set<Graph.Vertex<T, W>> visited) {
        Objects.requireNonNull(source, "Source is null");
        visited.add(source);
        visitor.visit(source);
        for (var next : graph.getEdges(source)) {
            Graph.Vertex<T, W> target = next.target();
            if (!visited.contains(target)) {
                dfs(graph, target, visitor, visited);
            }
        }
    }

    @Override
    public void bfs(Graph<T, W> graph, Algorithms.Visitor<T, W> visitor) {
        Set<Graph.Vertex<T, W>> visited = new HashSet<>();
        if (graph.size() == 0) {
            return;
        }
        var source = graph.ofIndex(0);
        Queue<Graph.Vertex<T, W>> queue = new LinkedList<>();
        queue.add(source);
        while (!queue.isEmpty()) {
            var current = queue.poll();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            visitor.visit(current);
            graph.getEdges(current).forEach(edge -> queue.add(edge.target()));
        }
    }

    @Override
    public List<Graph.Vertex<T, W>> searchCircle(Graph<T, W> graph) {
        Map<Graph.Vertex<T, W>, Graph.Vertex<T, W>> parents = new ConcurrentHashMap<>();
        Set<Graph.Vertex<T, W>> visited = new HashSet<>();
        for (Graph.Vertex<T, W> vertex : graph.getVertices()) {
            if (!visited.contains(vertex)) {
                var circle = searchCircle(graph, vertex, null, visited, parents);
                if (circle.isEmpty()) {
                    continue;
                }
                return circle;
            }
        }
        return List.of();
    }

    private List<Graph.Vertex<T, W>> searchCircle(Graph<T, W> graph, Graph.Vertex<T, W> vertex, Graph.Vertex<T, W> parent,
                                                  Set<Graph.Vertex<T, W>> visited, Map<Graph.Vertex<T, W>, Graph.Vertex<T, W>> parents) {
        if (visited.contains(vertex)) {
            return buildCircle(vertex, parent, parents);
        }
        visited.add(vertex);
        if (parent != null) {
            parents.put(vertex, parent);
        }
        for (var edge : graph.getEdges(vertex)) {
            var target = edge.target();
            if (target.equals(parent)) {
                continue;
            }
            List<Graph.Vertex<T, W>> circle = searchCircle(graph, target, vertex, visited, parents);
            if (!circle.isEmpty()) {
                return circle;
            }
        }
        return List.of();
    }

    private List<Graph.Vertex<T, W>> buildCircle(Graph.Vertex<T, W> vertex, Graph.Vertex<T, W> parent,
                                                 Map<Graph.Vertex<T, W>, Graph.Vertex<T, W>> parents) {
        List<Graph.Vertex<T, W>> circle = new ArrayList<>();
        circle.add(parent);
        Supplier<Graph.Vertex<T, W>> lastElement = () -> circle.get(circle.size() - 1);
        while (!lastElement.get().equals(vertex)) {
            circle.add(parents.get(lastElement.get()));
        }
        return circle;
    }
}
