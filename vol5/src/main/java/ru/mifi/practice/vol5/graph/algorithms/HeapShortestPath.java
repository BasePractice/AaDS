package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;
import ru.mifi.practice.vol6.tree.Priorities;

import java.util.HashMap;
import java.util.Map;

/**
 * Кратчайшие расстояния по Дейкстре на куче с уменьшением ключа из раздела 9. Все вершины лежат
 * в куче сразу: исходная с нулём, остальные с бесконечностью, — и найденное расстояние не кладёт
 * в очередь вторую запись о вершине, а уменьшает приоритет уже лежащей.
 *
 * <p>Отсюда и оценка O(m log n): в куче не больше n вершин, каждая дуга стоит одного уменьшения,
 * каждое уменьшение — O(log n). Очередь на копиях вершин хранит до m записей и разбирает
 * устаревшие уже после извлечения.
 */
public final class HeapShortestPath<T, W extends Number & Comparable<W>>
    implements Algorithms.ShortestDistance<T, W> {
    private final W maxDistance;
    private final W minDistance;
    private final Weight<W> weight;

    public HeapShortestPath(W maxDistance, W minDistance, Weight<W> weight) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.weight = weight;
    }

    @Override
    public Map<Graph.Vertex<T, W>, W> distances(Graph<T, W> graph, Graph.Vertex<T, W> source) {
        Map<Graph.Vertex<T, W>, W> distances = new HashMap<>();
        Priorities<Graph.Vertex<T, W>, W> queue = new Priorities.Binary<>();
        for (Graph.Vertex<T, W> vertex : graph.getVertices()) {
            W distance = vertex.equals(source) ? minDistance : maxDistance;
            distances.put(vertex, distance);
            queue.add(vertex, distance);
        }
        while (queue.size() > 0) {
            Graph.Vertex<T, W> vertex = queue.poll();
            W known = distances.get(vertex);
            if (known.equals(maxDistance)) {
                return distances;
            }
            relax(vertex, known, distances, queue);
        }
        return distances;
    }

    private void relax(Graph.Vertex<T, W> vertex,
                       W known,
                       Map<Graph.Vertex<T, W>, W> distances,
                       Priorities<Graph.Vertex<T, W>, W> queue) {
        for (Graph.Edge<T, W> edge : vertex.edges()) {
            Graph.Vertex<T, W> target = edge.target();
            W distance = weight.sum(known, edge.weight());
            if (queue.holds(target) && distance.compareTo(distances.get(target)) < 0) {
                distances.put(target, distance);
                queue.decrease(target, distance);
            }
        }
    }
}
