package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Кратчайшие расстояния между всеми парами вершин по алгоритму Флойда — Уоршелла. Вершины по
 * очереди пробуются как промежуточные: если через вершину k дорога из i в j короче известной, она
 * и запоминается. После круга по всем k таблица содержит кратчайшие пути с любыми промежуточными
 * вершинами.
 *
 * <p>Цена — O(n³) и таблица на n², зато отрицательные веса допустимы, а n запусков Дейкстры на
 * плотном графе обойдутся не дешевле. Пути через недостижимую вершину не складываются: сумма с
 * бесконечностью переполнила бы вес.
 */
public final class FloydWarshallShortestPath<T, W extends Number & Comparable<W>>
    implements Algorithms.AllShortestDistances<T, W> {
    private final W maxDistance;
    private final W minDistance;
    private final Weight<W> weight;

    public FloydWarshallShortestPath(W maxDistance, W minDistance, Weight<W> weight) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.weight = weight;
    }

    @Override
    public Map<Graph.Vertex<T, W>, Map<Graph.Vertex<T, W>, W>> distances(Graph<T, W> graph) {
        List<Graph.Vertex<T, W>> vertices = graph.getVertices();
        Map<Graph.Vertex<T, W>, Map<Graph.Vertex<T, W>, W>> distances = table(vertices);
        for (Graph.Vertex<T, W> middle : vertices) {
            for (Graph.Vertex<T, W> source : vertices) {
                relax(source, middle, vertices, distances);
            }
        }
        return distances;
    }

    private void relax(Graph.Vertex<T, W> source,
                       Graph.Vertex<T, W> middle,
                       List<Graph.Vertex<T, W>> vertices,
                       Map<Graph.Vertex<T, W>, Map<Graph.Vertex<T, W>, W>> distances) {
        W reach = distances.get(source).get(middle);
        if (reach.equals(maxDistance)) {
            return;
        }
        for (Graph.Vertex<T, W> target : vertices) {
            W rest = distances.get(middle).get(target);
            if (!rest.equals(maxDistance)) {
                W distance = weight.sum(reach, rest);
                if (distance.compareTo(distances.get(source).get(target)) < 0) {
                    distances.get(source).put(target, distance);
                }
            }
        }
    }

    private Map<Graph.Vertex<T, W>, Map<Graph.Vertex<T, W>, W>> table(List<Graph.Vertex<T, W>> vertices) {
        Map<Graph.Vertex<T, W>, Map<Graph.Vertex<T, W>, W>> distances = new HashMap<>();
        for (Graph.Vertex<T, W> source : vertices) {
            Map<Graph.Vertex<T, W>, W> row = new HashMap<>();
            vertices.forEach(target -> row.put(target, maxDistance));
            row.put(source, minDistance);
            distances.put(source, row);
        }
        vertices.forEach(source -> source.edges().forEach(edge -> shorten(distances.get(source), edge)));
        return distances;
    }

    private void shorten(Map<Graph.Vertex<T, W>, W> row, Graph.Edge<T, W> edge) {
        if (edge.weight().compareTo(row.get(edge.target())) < 0) {
            row.put(edge.target(), edge.weight());
        }
    }
}
