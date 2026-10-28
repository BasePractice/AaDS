package ru.mifi.practice.vol5.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Второе представление графа — матрица смежности V × V: в клетке [i][j] лежит вес дуги из i в j
 * или пусто, если дуги нет. Проверка «соединены ли две вершины» стоит O(1), зато память — O(V²)
 * при любом числе рёбер, а обход соседей вершины просматривает всю строку, O(V) вместо O(deg).
 * Поэтому матрица хороша для плотных графов и для алгоритмов, которые и так работают с таблицей
 * V × V, как Флойд — Уоршелл. Из параллельных дуг матрица помнит самую лёгкую.
 */
public final class AdjacencyMatrix<T, W extends Number & Comparable<W>> {
    private final Graph<T, W> graph;
    private final List<List<Optional<W>>> rows;

    public AdjacencyMatrix(Graph<T, W> graph) {
        this(graph, rows(graph));
    }

    private AdjacencyMatrix(Graph<T, W> graph, List<List<Optional<W>>> rows) {
        this.graph = graph;
        this.rows = rows;
    }

    private static <T, W extends Number & Comparable<W>> List<List<Optional<W>>> rows(Graph<T, W> graph) {
        List<List<Optional<W>>> rows = new ArrayList<>(graph.size());
        for (int i = 0; i < graph.size(); i++) {
            rows.add(new ArrayList<>(Collections.nCopies(graph.size(), Optional.empty())));
        }
        for (Graph.Vertex<T, W> vertex : graph.getVertices()) {
            for (Graph.Edge<T, W> edge : vertex.edges()) {
                List<Optional<W>> row = rows.get(vertex.index());
                int column = edge.target().index();
                if (row.get(column).map(known -> edge.weight().compareTo(known) < 0).orElse(true)) {
                    row.set(column, Optional.of(edge.weight()));
                }
            }
        }
        return rows;
    }

    public boolean adjacent(Graph.Vertex<T, W> source, Graph.Vertex<T, W> target) {
        return weight(source, target).isPresent();
    }

    public Optional<W> weight(Graph.Vertex<T, W> source, Graph.Vertex<T, W> target) {
        return rows.get(source.index()).get(target.index());
    }

    public List<Graph.Vertex<T, W>> neighbours(Graph.Vertex<T, W> source) {
        List<Graph.Vertex<T, W>> neighbours = new ArrayList<>();
        List<Optional<W>> row = rows.get(source.index());
        for (int column = 0; column < row.size(); column++) {
            if (row.get(column).isPresent()) {
                neighbours.add(graph.getVertex(column));
            }
        }
        return neighbours;
    }

    public int size() {
        return rows.size();
    }
}
