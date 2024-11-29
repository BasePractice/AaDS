package ru.mifi.practice.vol5.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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
        List<String> searchCircle(Graph<T, W> graph);
    }

    @FunctionalInterface
    interface ShortestPath<T, W extends Number & Comparable<W>> {

        Map<String, W> distances(Graph<T, W> graph, String source);
    }

    final class DijkstraShortestPath<T, W extends Number & Comparable<W>> implements ShortestPath<T, W> {
        private final W maxDistance;
        private final W minDistance;
        private final Sum<W> sum;

        public DijkstraShortestPath(W maxDistance, W minDistance, Sum<W> sum) {
            this.maxDistance = maxDistance;
            this.minDistance = minDistance;
            this.sum = sum;
        }

        @Override
        public Map<String, W> distances(Graph<T, W> graph, String source) {
            Map<String, W> distances = new HashMap<>();
            Set<String> vertices = graph.getVertices();
            vertices.forEach(vertex -> {
                distances.put(vertex, maxDistance);
            });
            distances.put(source, minDistance);
            final class D {
                private final String source;
                private final W distance;

                D(String source, W distance) {
                    this.source = source;
                    this.distance = distance;
                }
            }

            Queue<D> pq = new PriorityQueue<>(Comparator.comparing(o -> o.distance));
            pq.add(new D(source, minDistance));
            Set<String> visited = new HashSet<>();
            while (!pq.isEmpty()) {
                final D d = pq.poll();
                if (visited.contains(d.source)) {
                    continue;
                }
                visited.add(d.source);
                for (var edge : graph.getEdges(d.source)) {
                    Graph.Vertex<T, W> target = edge.target();
                    if (visited.contains(target.id())) {
                        continue;
                    }
                    W w = distances.get(target.id());
                    W distance = sum.sum(d.distance, edge.weight());
                    if (distance.compareTo(w) < 0) {
                        distances.put(target.id(), distance);
                        pq.offer(new D(target.id(), distance));
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

    final class Default<T, W extends Number & Comparable<W>>
        implements CircleSearch<T, W>, FirstSearch<T, W> {
        private static final String NONE = UUID.randomUUID().toString();

        @Override
        public void dfs(Graph<T, W> graph, Visitor<T, W> visitor) {
            Iterator<String> it = graph.getVertices().iterator();
            if (it.hasNext()) {
                Map<String, Boolean> visited = new ConcurrentHashMap<>();
                dfs(graph, it.next(), visitor, visited);
            }
        }

        private void dfs(Graph<T, W> graph, String source, Visitor<T, W> visitor, Map<String, Boolean> visited) {
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
        public void bfs(Graph<T, W> graph, Visitor<T, W> visitor) {
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
}
