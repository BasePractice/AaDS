package ru.mifi.practice.boring.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Мосты и точки сочленения — алгоритм Тарьяна. Мост — ребро, без которого граф распадается;
 * точка сочленения — вершина с тем же свойством. Проверять каждое ребро отдельным обходом стоило
 * бы O(m·(n+m)); Тарьян находит всё за один поиск в глубину.
 *
 * <p>Обход в глубину нумерует вершины по времени входа. Для каждой вершины считается низшая
 * достижимая метка: самое раннее время входа, куда можно попасть из её поддерева, спустившись по
 * рёбрам дерева и не более чем один раз поднявшись по обратному ребру.
 *
 * <p>Отсюда оба ответа. Ребро в сына — мост, когда метка сына строго больше времени входа
 * родителя: значит из поддерева сына назад пути нет. Вершина — точка сочленения, когда у неё
 * есть сын с меткой не меньше её собственного времени входа; корень обхода — особый случай, он
 * точка сочленения ровно тогда, когда у него больше одного сына в дереве обхода.
 */
public final class LowLink {

    /** Мосты неориентированного графа, каждый по одному разу. */
    public List<Graph.Edge> bridges(Graph graph) {
        Search search = new Search(graph);
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (search.times[vertex] == 0) {
                search.walk(vertex, -1);
            }
        }
        return search.bridges;
    }

    /** Точки сочленения неориентированного графа. */
    public Set<Integer> articulations(Graph graph) {
        Search search = new Search(graph);
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (search.times[vertex] == 0) {
                search.walk(vertex, -1);
            }
        }
        return search.articulations;
    }

    /** Состояние одного обхода: времена входа, низшие метки и найденное. */
    private static final class Search {
        private final Graph graph;
        private final int[] times;
        private final int[] lowest;
        private final List<Graph.Edge> bridges = new ArrayList<>();
        private final Set<Integer> articulations = new LinkedHashSet<>();
        private int clock;

        Search(Graph graph) {
            this.graph = graph;
            this.times = new int[graph.size()];
            this.lowest = new int[graph.size()];
            Arrays.fill(lowest, Integer.MAX_VALUE);
        }

        void walk(int vertex, int parent) {
            ++clock;
            times[vertex] = clock;
            lowest[vertex] = clock;
            int children = 0;
            boolean skipped = false;
            for (Graph.Edge edge : graph.edges(vertex)) {
                if (edge.to() == parent && !skipped) {
                    skipped = true;
                    continue;
                }
                if (times[edge.to()] == 0) {
                    ++children;
                    walk(edge.to(), vertex);
                    lowest[vertex] = Math.min(lowest[vertex], lowest[edge.to()]);
                    mark(vertex, parent, edge);
                } else {
                    lowest[vertex] = Math.min(lowest[vertex], times[edge.to()]);
                }
            }
            if (parent < 0 && children > 1) {
                articulations.add(vertex);
            }
        }

        private void mark(int vertex, int parent, Graph.Edge edge) {
            if (lowest[edge.to()] > times[vertex]) {
                bridges.add(edge);
            }
            if (parent >= 0 && lowest[edge.to()] >= times[vertex]) {
                articulations.add(vertex);
            }
        }
    }
}
