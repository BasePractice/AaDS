package ru.mifi.practice.boring.graph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Кратчайшие пути между всеми парами по Джонсону. Флойд — Уоршелл решает ту же задачу за O(n³) и
 * не замечает, разрежен граф или плотен. Джонсон же n раз запускает Дейкстру — O(n·m·log n), а на
 * разрежённом графе это заметно быстрее. Беда в том, что Дейкстра не работает с отрицательными
 * весами, а именно ради них Флойда обычно и берут.
 *
 * <p>Выход — перевес. К графу добавляется фиктивная вершина с нулевыми дугами во все остальные, и
 * Беллман — Форд считает от неё потенциалы. Новый вес дуги — старый плюс потенциал начала минус
 * потенциал конца; он неотрицателен, а длины путей меняются на одну и ту же величину, зависящую
 * только от концов пути. Поэтому кратчайшие пути остаются кратчайшими, а Дейкстра работать может.
 *
 * <p>После расчёта потенциалы снимаются обратно. Если Беллман — Форд обнаружил отрицательный
 * цикл, задача не имеет решения, и это отказ, а не молчаливо неверный ответ.
 */
public final class Johnson {

    /** Таблица расстояний; недостижимые пары помечены наибольшим целым. */
    public int[][] distances(Graph graph) {
        int[] potentials = potentials(graph);
        int[][] distances = new int[graph.size()][];
        for (int source = 0; source < graph.size(); source++) {
            distances[source] = dijkstra(graph, source, potentials);
        }
        return distances;
    }

    /** Потенциалы Беллмана — Форда от фиктивной вершины, связанной со всеми нулевыми дугами. */
    private int[] potentials(Graph graph) {
        int[] potentials = new int[graph.size()];
        for (int round = 0; round < graph.size(); round++) {
            boolean changed = false;
            for (Graph.Edge edge : graph.edges()) {
                if (potentials[edge.from()] + edge.weight() < potentials[edge.to()]) {
                    potentials[edge.to()] = potentials[edge.from()] + edge.weight();
                    changed = true;
                }
            }
            if (!changed) {
                return potentials;
            }
        }
        throw new IllegalStateException("Negative cycle leaves the shortest path undefined");
    }

    private int[] dijkstra(Graph graph, int source, int[] potentials) {
        int[] distances = new int[graph.size()];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;
        boolean[] closed = new boolean[graph.size()];
        Queue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(pair -> pair[1]));
        queue.add(new int[]{source, 0});
        while (!queue.isEmpty()) {
            int vertex = queue.poll()[0];
            if (closed[vertex]) {
                continue;
            }
            closed[vertex] = true;
            for (Graph.Edge edge : graph.edges(vertex)) {
                relax(edge, potentials, distances, closed, queue);
            }
        }
        return restored(distances, source, potentials);
    }

    private void relax(Graph.Edge edge, int[] potentials, int[] distances, boolean[] closed, Queue<int[]> queue) {
        int weight = edge.weight() + potentials[edge.from()] - potentials[edge.to()];
        int distance = distances[edge.from()] + weight;
        if (!closed[edge.to()] && distance < distances[edge.to()]) {
            distances[edge.to()] = distance;
            queue.add(new int[]{edge.to(), distance});
        }
    }

    /** Снятие потенциалов: длина пути возвращается к исходным весам дуг. */
    private int[] restored(int[] distances, int source, int[] potentials) {
        int[] restored = distances.clone();
        for (int target = 0; target < restored.length; target++) {
            if (restored[target] != Integer.MAX_VALUE) {
                restored[target] += potentials[target] - potentials[source];
            }
        }
        return restored;
    }
}
