package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Base<T, W extends Number & Comparable<W>>
    implements Algorithms.CircleSearch<T, W>, Algorithms.FirstSearch<T, W> {
    private static final String NONE = UUID.randomUUID().toString();

    @Override
    public void dfs(Graph<T, W> graph, Algorithms.Visitor<T, W> visitor) {
        Iterator<String> it = graph.getVertices().iterator();
        if (it.hasNext()) {
            Map<String, Boolean> visited = new ConcurrentHashMap<>();
            dfs(graph, it.next(), visitor, visited);
        }
    }

    private void dfs(Graph<T, W> graph, String source, Algorithms.Visitor<T, W> visitor, Map<String, Boolean> visited) {
        Graph.Vertex<T, W> vertex = graph.getVertex(source);
        Objects.requireNonNull(vertex, "Vertex is null");
        visited.put(source, true);
        visitor.visit(vertex);
        for (var next : graph.getEdges(source)) {
            String target = next.target().id();
            Boolean v = visited.getOrDefault(target, false);
            if (!v) {
                dfs(graph, target, visitor, visited);
            }
        }
    }

    @Override
    public void bfs(Graph<T, W> graph, Algorithms.Visitor<T, W> visitor) {
        Map<String, Boolean> visited = new ConcurrentHashMap<>();
        Iterator<String> it = graph.getVertices().iterator();
        if (!it.hasNext()) {
            return;
        }
        String source = it.next();
        Queue<String> queue = new LinkedList<>();
        queue.add(source);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (visited.containsKey(current)) {
                continue;
            }
            Graph.Vertex<T, W> vertex = graph.getVertex(current);
            visited.put(current, true);
            visitor.visit(vertex);
            graph.getEdges(current).forEach(edge -> queue.add(edge.target().id()));
        }
    }

    @Override
    public List<String> searchCircle(Graph<T, W> graph) {
        Map<String, Boolean> visited = new ConcurrentHashMap<>();
        Map<String, String> parents = new ConcurrentHashMap<>();
        for (String vertex : graph.getVertices()) {
            parents.put(vertex, NONE);
            visited.put(vertex, false);
        }
        for (String vertex : graph.getVertices()) {
            if (!visited.getOrDefault(vertex, false)) {
                var circle = searchCircle(graph, vertex, NONE, visited, parents);
                if (circle.isEmpty()) {
                    continue;
                }
                return circle;
            }
        }
        return List.of();
    }

    private List<String> searchCircle(Graph<T, W> graph, String vertex, String parent,
                                      Map<String, Boolean> visited, Map<String, String> parents) {
        if (visited.getOrDefault(vertex, false)) {
            return buildCircle(vertex, parent, parents);
        }
        visited.put(vertex, true);
        parents.put(vertex, parent);
        for (var edge : graph.getEdges(vertex)) {
            String target = edge.target().id();
            if (target.equals(parent)) {
                continue;
            }
            List<String> circle = searchCircle(graph, target, vertex, visited, parents);
            if (!circle.isEmpty()) {
                return circle;
            }
        }
        return List.of();
    }

    private List<String> buildCircle(String vertex, String parent, Map<String, String> parents) {
        List<String> circle = new ArrayList<>();
        circle.add(parent);
        Supplier<String> lastElement = () -> circle.get(circle.size() - 1);
        while (!lastElement.get().equals(vertex)) {
            circle.add(parents.get(lastElement.get()));
        }
        return circle;
    }
}
