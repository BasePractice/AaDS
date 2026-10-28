package ru.mifi.practice.boring.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 * Перестановка Катхилла — Макки: нумерует вершины так, чтобы ненулевые элементы матрицы
 * смежности сбились к диагонали. Ширина ленты — наибольшая разница номеров у смежных вершин;
 * чем она меньше, тем меньше памяти и работы нужно решателям систем уравнений, которые хранят
 * только ленту, а не всю матрицу.
 *
 * <p>Устроено как поиск в ширину с одной добавкой: соседи ставятся в очередь не как попало, а по
 * возрастанию степени. Начинают с вершины наименьшей степени — с края графа, а не из середины,
 * иначе номера расползутся в обе стороны.
 *
 * <p>Обратный порядок — тот же список задом наперёд. Он обычно даёт ленту не хуже прямого, а на
 * практике заметно лучше, поэтому в библиотеках по умолчанию берут именно его.
 */
public final class CuthillMcKee {

    /** Новые номера вершин: ячейка i — вершина, которая станет i-й. */
    public int[] order(Graph graph) {
        boolean[] taken = new boolean[graph.size()];
        List<Integer> order = new ArrayList<>();
        for (int start = quietest(graph, taken); start >= 0; start = quietest(graph, taken)) {
            walk(graph, start, taken, order);
        }
        int[] numbers = new int[order.size()];
        for (int at = 0; at < order.size(); at++) {
            numbers[at] = order.get(at);
        }
        return numbers;
    }

    /** Обратный порядок Катхилла — Макки: тот же обход, записанный с конца. */
    public int[] reversed(Graph graph) {
        int[] order = order(graph);
        int[] reversed = new int[order.length];
        for (int at = 0; at < order.length; at++) {
            reversed[at] = order[order.length - 1 - at];
        }
        return reversed;
    }

    /** Ширина ленты при заданной нумерации: наибольшая разница номеров смежных вершин. */
    public int bandwidth(Graph graph, int[] order) {
        int[] numbers = new int[order.length];
        for (int at = 0; at < order.length; at++) {
            numbers[order[at]] = at;
        }
        int bandwidth = 0;
        for (Graph.Edge edge : graph.edges()) {
            bandwidth = Math.max(bandwidth, Math.abs(numbers[edge.from()] - numbers[edge.to()]));
        }
        return bandwidth;
    }

    private void walk(Graph graph, int start, boolean[] taken, List<Integer> order) {
        Deque<Integer> pending = new ArrayDeque<>(List.of(start));
        taken[start] = true;
        while (!pending.isEmpty()) {
            int vertex = pending.poll();
            order.add(vertex);
            for (int neighbour : neighbours(graph, vertex, taken)) {
                taken[neighbour] = true;
                pending.add(neighbour);
            }
        }
    }

    /** Ещё не занятые соседи вершины по возрастанию степени. */
    private List<Integer> neighbours(Graph graph, int vertex, boolean[] taken) {
        List<Integer> neighbours = new ArrayList<>();
        for (Graph.Edge edge : graph.edges(vertex)) {
            if (!taken[edge.to()] && !neighbours.contains(edge.to())) {
                neighbours.add(edge.to());
            }
        }
        neighbours.sort(Comparator.comparingInt(graph::degree));
        return neighbours;
    }

    /** Незанятая вершина наименьшей степени или −1, если занятыми оказались все. */
    private int quietest(Graph graph, boolean[] taken) {
        int quietest = -1;
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (!taken[vertex] && (quietest < 0 || graph.degree(vertex) < graph.degree(quietest))) {
                quietest = vertex;
            }
        }
        return quietest;
    }
}
