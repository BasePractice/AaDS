package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;

import java.util.HashMap;
import java.util.Map;

/**
 * Кратчайшие расстояния от исходной вершины по алгоритму Беллмана — Форда. В отличие от Дейкстры
 * держит отрицательные веса: та жадно закрывает вершину на минимуме и уже не пересматривает её,
 * а здесь все дуги перебираются заново, пока расстояния меняются, — не более n−1 круга, потому
 * что кратчайший путь без циклов длиннее n−1 дуги быть не может.
 *
 * <p>Круг после последнего — проверка: если расстояние всё ещё уменьшается, в графе есть цикл
 * отрицательного веса и кратчайшего пути просто не существует. Цена — O(n·m) против O(m log n)
 * у Дейкстры.
 */
public final class BellmanFordShortestPath<T, W extends Number & Comparable<W>>
    implements Algorithms.ShortestDistance<T, W> {
    private final W maxDistance;
    private final W minDistance;
    private final Weight<W> weight;

    public BellmanFordShortestPath(W maxDistance, W minDistance, Weight<W> weight) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.weight = weight;
    }

    @Override
    public Map<Graph.Vertex<T, W>, W> distances(Graph<T, W> graph, Graph.Vertex<T, W> source) {
        Map<Graph.Vertex<T, W>, W> distances = new HashMap<>();
        graph.getVertices().forEach(vertex -> distances.put(vertex, maxDistance));
        distances.put(source, minDistance);
        for (int round = 1; round < graph.size(); round++) {
            if (!relaxed(graph, distances)) {
                return distances;
            }
        }
        if (relaxed(graph, distances)) {
            throw new IllegalStateException("Negative cycle leaves the shortest path undefined");
        }
        return distances;
    }

    private boolean relaxed(Graph<T, W> graph, Map<Graph.Vertex<T, W>, W> distances) {
        boolean changed = false;
        for (Graph.Vertex<T, W> vertex : graph.getVertices()) {
            W known = distances.get(vertex);
            if (!known.equals(maxDistance) && relaxed(vertex, known, distances)) {
                changed = true;
            }
        }
        return changed;
    }

    private boolean relaxed(Graph.Vertex<T, W> vertex, W known, Map<Graph.Vertex<T, W>, W> distances) {
        boolean changed = false;
        for (Graph.Edge<T, W> edge : vertex.edges()) {
            W distance = weight.sum(known, edge.weight());
            if (distance.compareTo(distances.get(edge.target())) < 0) {
                distances.put(edge.target(), distance);
                changed = true;
            }
        }
        return changed;
    }
}
