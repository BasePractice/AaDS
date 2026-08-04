package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/** Кратчайшие расстояния от исходной вершины по алгоритму Дейкстры. */
public final class DijkstraShortestPath<T, W extends Number & Comparable<W>> implements Algorithms.ShortestDistance<T, W> {
    private final W maxDistance;
    private final W minDistance;
    private final Weight<W> weight;

    public DijkstraShortestPath(W maxDistance, W minDistance, Weight<W> weight) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.weight = weight;
    }

    @Override
    public Map<Graph.Vertex<T, W>, W> distances(Graph<T, W> graph, Graph.Vertex<T, W> source) {
        Map<Graph.Vertex<T, W>, W> distances = new HashMap<>();
        List<Graph.Vertex<T, W>> vertices = graph.getVertices();
        vertices.forEach(vertex -> distances.put(vertex, maxDistance));
        distances.put(source, minDistance);
        final class Tuple {
            private final Graph.Vertex<T, W> source;
            private final W distance;

            Tuple(Graph.Vertex<T, W> source, W distance) {
                this.source = source;
                this.distance = distance;
            }
        }

        Queue<Tuple> queue = new PriorityQueue<>(Comparator.comparing(tuple -> tuple.distance));
        queue.add(new Tuple(source, minDistance));
        Set<Graph.Vertex<T, W>> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            final Tuple tuple = queue.poll();
            if (visited.contains(tuple.source)) {
                continue;
            }
            visited.add(tuple.source);
            for (var edge : tuple.source.edges()) {
                Graph.Vertex<T, W> target = edge.target();
                if (visited.contains(target)) {
                    continue;
                }
                W known = distances.get(target);
                W distance = weight.sum(tuple.distance, edge.weight());
                if (distance.compareTo(known) < 0) {
                    distances.put(target, distance);
                    queue.offer(new Tuple(target, distance));
                }
            }
        }
        return distances;
    }
}
