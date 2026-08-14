package ru.mifi.practice.boring.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Граф с вершинами от нуля до n−1 и взвешенными дугами — одна структура на все алгоритмы пакета.
 * Хранится списками смежности: для каждой вершины — список выходящих из неё дуг. Матрица
 * смежности была бы проще, но заняла бы n² памяти и заставила бы каждый обход перебирать все
 * вершины вместо реальных соседей.
 *
 * <p>Граф неизменяем: дуга добавляется методом, возвращающим новый граф. Дуга направленная;
 * неориентированное ребро — это две дуги, и метод both добавляет обе сразу. Такое разделение
 * важно: мосты и эйлеров цикл ищут в неориентированном графе, поток и кратчайшие пути — в
 * направленном.
 */
public final class Graph {
    private final List<List<Edge>> outgoing;

    public Graph(int size) {
        this(empty(size));
    }

    private Graph(List<List<Edge>> outgoing) {
        this.outgoing = outgoing;
    }

    /** Направленная дуга из одной вершины в другую. */
    public Graph with(int from, int to, int weight) {
        List<List<Edge>> copy = copy();
        copy.get(from).add(new Edge(from, to, weight));
        return new Graph(copy);
    }

    /** Неориентированное ребро — две дуги в обе стороны. */
    public Graph both(int from, int to, int weight) {
        List<List<Edge>> copy = copy();
        copy.get(from).add(new Edge(from, to, weight));
        copy.get(to).add(new Edge(to, from, weight));
        return new Graph(copy);
    }

    public int size() {
        return outgoing.size();
    }

    /** Дуги, выходящие из вершины. */
    public List<Edge> edges(int vertex) {
        return List.copyOf(outgoing.get(vertex));
    }

    /** Все дуги графа в порядке добавления по вершинам. */
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        outgoing.forEach(edges::addAll);
        return edges;
    }

    public int degree(int vertex) {
        return outgoing.get(vertex).size();
    }

    private List<List<Edge>> copy() {
        List<List<Edge>> copy = new ArrayList<>();
        outgoing.forEach(edges -> copy.add(new ArrayList<>(edges)));
        return copy;
    }

    private static List<List<Edge>> empty(int size) {
        List<List<Edge>> outgoing = new ArrayList<>();
        for (int vertex = 0; vertex < size; vertex++) {
            outgoing.add(new ArrayList<>());
        }
        return outgoing;
    }

    @Override
    public String toString() {
        return "граф из " + size() + " вершин и " + edges().size() + " дуг";
    }

    /** Дуга: откуда, куда и во сколько обходится. */
    public record Edge(int from, int to, int weight) {
    }
}
