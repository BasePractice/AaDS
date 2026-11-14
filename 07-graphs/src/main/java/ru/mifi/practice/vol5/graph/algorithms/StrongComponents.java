package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Разбиение ориентированного графа на компоненты алгоритмом Косарайю.
 */
public final class StrongComponents {
    public record Arc(int source, int target) {
    }

    public record Result(List<Set<String>> components, Set<Arc> arcs) {
        public Result {
            components = components.stream()
                .map(labels -> Collections.unmodifiableSet(new LinkedHashSet<>(labels)))
                .toList();
            arcs = Collections.unmodifiableSet(new LinkedHashSet<>(arcs));
        }
    }

    public <T, W extends Number & Comparable<W>> Result find(Graph<T, W> graph) {
        List<Integer> order = new ArrayList<>(graph.size());
        boolean[] visited = new boolean[graph.size()];
        for (var vertex : graph.getVertices()) {
            if (!visited[vertex.index()]) {
                finish(vertex, visited, order);
            }
        }
        List<List<Integer>> reverse = transpose(graph);
        int[] membership = new int[graph.size()];
        Arrays.fill(membership, -1);
        List<Set<String>> components = new ArrayList<>();
        for (int index = order.size() - 1; index >= 0; index--) {
            int vertex = order.get(index);
            if (membership[vertex] == -1) {
                Set<String> labels = new LinkedHashSet<>();
                collect(graph, reverse, vertex, membership, components.size(), labels);
                components.add(labels);
            }
        }
        return new Result(components, condense(graph, membership));
    }

    private <T, W extends Number & Comparable<W>> void finish(
        Graph.Vertex<T, W> vertex, boolean[] visited, List<Integer> order
    ) {
        visited[vertex.index()] = true;
        for (var edge : vertex.edges()) {
            if (!visited[edge.target().index()]) {
                finish(edge.target(), visited, order);
            }
        }
        order.add(vertex.index());
    }

    private <T, W extends Number & Comparable<W>> List<List<Integer>> transpose(Graph<T, W> graph) {
        List<List<Integer>> reverse = new ArrayList<>(graph.size());
        for (int index = 0; index < graph.size(); index++) {
            reverse.add(new ArrayList<>());
        }
        for (var vertex : graph.getVertices()) {
            for (var edge : vertex.edges()) {
                reverse.get(edge.target().index()).add(vertex.index());
            }
        }
        return reverse;
    }

    private <T, W extends Number & Comparable<W>> void collect(
        Graph<T, W> graph, List<List<Integer>> reverse, int vertex, int[] membership, int component, Set<String> labels
    ) {
        membership[vertex] = component;
        labels.add(graph.getVertex(vertex).label());
        for (int target : reverse.get(vertex)) {
            if (membership[target] == -1) {
                collect(graph, reverse, target, membership, component, labels);
            }
        }
    }

    private <T, W extends Number & Comparable<W>> Set<Arc> condense(Graph<T, W> graph, int[] membership) {
        Set<Arc> arcs = new LinkedHashSet<>();
        for (var vertex : graph.getVertices()) {
            int source = membership[vertex.index()];
            for (var edge : vertex.edges()) {
                int target = membership[edge.target().index()];
                if (source != target) {
                    arcs.add(new Arc(source, target));
                }
            }
        }
        return arcs;
    }
}
