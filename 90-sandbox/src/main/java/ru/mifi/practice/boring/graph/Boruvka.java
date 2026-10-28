package ru.mifi.practice.boring.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Минимальное остовное дерево по Борувке — самый старый из трёх классических алгоритмов, 1926
 * год. Крускал добавляет рёбра по одному в порядке веса, Прим растит одно дерево; Борувка растит
 * все деревья сразу: на каждом круге каждая компонента выбирает своё самое лёгкое исходящее
 * ребро, и все выбранные рёбра добавляются разом.
 *
 * <p>За круг число компонент уменьшается хотя бы вдвое — каждая с кем-то сливается, — поэтому
 * кругов не больше log n. Отсюда и любовь к нему в параллельных вычислениях: выбор лёгкого ребра
 * каждая компонента делает независимо от других, а это распараллеливается.
 *
 * <p>Компоненты хранит система непересекающихся множеств. Чтобы алгоритм не заклинило на рёбрах
 * одинакового веса — две компоненты выбрали бы друг друга и образовали цикл, — веса
 * упорядочиваются с оглядкой на номер ребра: при равном весе меньшим считается ребро,
 * добавленное раньше.
 */
public final class Boruvka {

    public List<Graph.Edge> tree(Graph graph) {
        List<Graph.Edge> edges = graph.edges();
        int[] components = components(graph.size());
        List<Graph.Edge> tree = new ArrayList<>();
        int[] cheapest = new int[graph.size()];
        while (tree.size() < graph.size() - 1) {
            Arrays.fill(cheapest, -1);
            for (int at = 0, size = edges.size(); at < size; at++) {
                elect(edges, components, cheapest, at);
            }
            if (!joined(edges, components, cheapest, tree)) {
                return tree;
            }
        }
        return tree;
    }

    /** Ребро претендует на звание самого лёгкого исходящего для своей компоненты. */
    private void elect(List<Graph.Edge> edges, int[] components, int[] cheapest, int at) {
        Graph.Edge edge = edges.get(at);
        int from = root(components, edge.from());
        int to = root(components, edge.to());
        if (from == to) {
            return;
        }
        for (int component : new int[]{from, to}) {
            if (cheapest[component] < 0 || lighter(edge, edges.get(cheapest[component]))) {
                cheapest[component] = at;
            }
        }
    }

    /** Слияние компонент выбранными рёбрами; ответ ложен, если сливать больше нечего. */
    private boolean joined(List<Graph.Edge> edges, int[] components, int[] cheapest, List<Graph.Edge> tree) {
        boolean joined = false;
        for (int chosen : cheapest) {
            if (chosen < 0) {
                continue;
            }
            Graph.Edge edge = edges.get(chosen);
            if (root(components, edge.from()) != root(components, edge.to())) {
                components[root(components, edge.from())] = root(components, edge.to());
                tree.add(edge);
                joined = true;
            }
        }
        return joined;
    }

    private boolean lighter(Graph.Edge edge, Graph.Edge other) {
        return edge.weight() < other.weight();
    }

    private int root(int[] components, int vertex) {
        int root = vertex;
        while (components[root] != root) {
            root = components[root];
        }
        components[vertex] = root;
        return root;
    }

    private int[] components(int size) {
        int[] components = new int[size];
        for (int vertex = 0; vertex < size; vertex++) {
            components[vertex] = vertex;
        }
        return components;
    }
}
