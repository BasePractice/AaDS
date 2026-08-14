package ru.mifi.practice.boring.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Эйлеров цикл по Хирхольцеру: замкнутый маршрут, проходящий по каждому ребру ровно раз. Задача
 * та самая, с кёнигсбергскими мостами: цикл есть тогда и только тогда, когда граф связен и у
 * каждой вершины чётная степень.
 *
 * <p>Алгоритм устроен красиво: идём куда глаза глядят, вычёркивая пройденные рёбра, пока не
 * упрёмся. Упереться мы можем только в стартовую вершину — из любой другой при чётных степенях
 * всегда есть выход. Получился цикл, но, возможно, не все рёбра пройдены; тогда на нём берётся
 * вершина с неиспользованными рёбрами, и из неё вплетается новый цикл.
 *
 * <p>Реализуется это стеком без единой рекурсии: вершина уходит в ответ, только когда все её
 * рёбра исчерпаны, поэтому вплетение получается само собой. Каждая вершина помнит, до какой
 * своей дуги уже дошла, поэтому суммарно рёбра просматриваются один раз — O(рёбер).
 *
 * <p>Связность отдельно не проверяется: если рёбра разбросаны по разным компонентам, маршрут
 * выйдет короче, чем всё множество рёбер, и это видно по длине ответа.
 */
public final class Hierholzer {

    /**
     * Эйлеров цикл в неориентированном графе — последовательность вершин, где первая совпадает с
     * последней. Пустой список, если цикла нет.
     */
    public List<Integer> circuit(Graph graph) {
        List<Graph.Edge> edges = graph.edges();
        if (edges.isEmpty() || odd(graph)) {
            return List.of();
        }
        int[] twins = twins(graph, edges);
        List<List<Integer>> outgoing = outgoing(graph, edges);
        boolean[] used = new boolean[edges.size()];
        int[] reached = new int[graph.size()];
        Deque<Integer> pending = new ArrayDeque<>(List.of(edges.get(0).from()));
        List<Integer> circuit = new ArrayList<>();
        while (!pending.isEmpty()) {
            int vertex = pending.peek();
            int edge = unused(outgoing.get(vertex), used, reached, vertex);
            if (edge < 0) {
                circuit.add(pending.pop());
            } else {
                used[edge] = true;
                used[twins[edge]] = true;
                pending.push(edges.get(edge).to());
            }
        }
        Collections.reverse(circuit);
        return circuit.size() == edges.size() / 2 + 1 ? circuit : List.of();
    }

    /** Первая непройденная дуга вершины; указатель сдвигается, чтобы не смотреть дважды. */
    private int unused(List<Integer> outgoing, boolean[] used, int[] reached, int vertex) {
        while (reached[vertex] < outgoing.size() && used[outgoing.get(reached[vertex])]) {
            reached[vertex]++;
        }
        return reached[vertex] < outgoing.size() ? outgoing.get(reached[vertex]) : -1;
    }

    /** Для каждой дуги — номер обратной ей: пройдя ребро, вычёркиваем обе его стороны. */
    private int[] twins(Graph graph, List<Graph.Edge> edges) {
        Map<Long, Deque<Integer>> waiting = new HashMap<>();
        int[] twins = new int[edges.size()];
        for (int at = 0; at < twins.length; at++) {
            twins[at] = at;
        }
        for (int at = 0; at < edges.size(); at++) {
            Graph.Edge edge = edges.get(at);
            Deque<Integer> pending = waiting.get(key(graph, edge.to(), edge.from()));
            if (pending == null || pending.isEmpty()) {
                waiting.computeIfAbsent(key(graph, edge.from(), edge.to()), k -> new ArrayDeque<>()).add(at);
            } else {
                int twin = pending.poll();
                twins[at] = twin;
                twins[twin] = at;
            }
        }
        return twins;
    }

    private long key(Graph graph, int from, int to) {
        return (long) from * graph.size() + to;
    }

    private List<List<Integer>> outgoing(Graph graph, List<Graph.Edge> edges) {
        List<List<Integer>> outgoing = new ArrayList<>();
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            outgoing.add(new ArrayList<>());
        }
        for (int at = 0; at < edges.size(); at++) {
            outgoing.get(edges.get(at).from()).add(at);
        }
        return outgoing;
    }

    private boolean odd(Graph graph) {
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (graph.degree(vertex) % 2 != 0) {
                return true;
            }
        }
        return false;
    }
}
