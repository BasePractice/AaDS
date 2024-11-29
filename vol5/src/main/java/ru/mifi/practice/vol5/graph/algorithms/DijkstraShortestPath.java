package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public final class DijkstraShortestPath<T, W extends Number & Comparable<W>>
    implements Algorithms.ShortestDistance<T, W> {
    private final W maxDistance;
    private final W minDistance;
    private final Sum<W> sum;

    public DijkstraShortestPath(W maxDistance, W minDistance, Sum<W> sum) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.sum = sum;
    }

    @Override
    public Map<Graph.Vertex<T, W>, W> distances(Graph<T, W> graph, String source) {
        Map<Graph.Vertex<T, W>, W> distances = new HashMap<>();
        List<Graph.Vertex<T, W>> vertices = graph.getVertices();
        vertices.forEach(vertex -> {
            distances.put(vertex, maxDistance);
        });
        Graph.Vertex<T, W> sourceVertex = graph.getVertex(source);
        distances.put(sourceVertex, minDistance);
        final class D {
            private final Graph.Vertex<T, W> source;
            private final W distance;

            D(Graph.Vertex<T, W> source, W distance) {
                this.source = source;
                this.distance = distance;
            }
        }

        Queue<D> pq = new PriorityQueue<>(Comparator.comparing(o -> o.distance));
        pq.add(new D(sourceVertex, minDistance));
        Set<Graph.Vertex<T, W>> visited = new HashSet<>();
        while (!pq.isEmpty()) {
            final D d = pq.poll();
            if (visited.contains(d.source)) {
                continue;
            }
            visited.add(d.source);
            for (var edge : graph.getEdges(d.source)) {
                Graph.Vertex<T, W> target = edge.target();
                if (visited.contains(target)) {
                    continue;
                }
                W w = distances.get(target);
                W distance = sum.sum(d.distance, edge.weight());
                if (distance.compareTo(w) < 0) {
                    distances.put(target, distance);
                    pq.offer(new D(target, distance));
                }
            }
        }
        return distances;
    }

    @FunctionalInterface
    public interface Sum<W> {
        W sum(W v1, W v2);
    }
}
