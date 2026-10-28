package ru.mifi.practice.vol12;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.vol10.DisjointSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Минимальное остовное дерево из раздела 12 курса: связный подграф без циклов, содержащий все
 * вершины и имеющий минимальную сумму весов. Жадность работает благодаря свойству разреза:
 * самое лёгкое ребро любого разреза входит в некоторое минимальное остовное дерево.
 */
public interface SpanningTree {

    /**
     * Рёбра остовного дерева. Для несвязного графа возвращается остовный лес — рёбер меньше,
     * чем вершин минус одна.
     */
    List<Edge> build(Graph graph, Counter counter);

    record Edge(int source, int target, int weight) {
    }

    /**
     * Неориентированный взвешенный граф: список рёбер плюс списки смежности, чтобы обеим
     * реализациям было удобно.
     */
    final class Graph {
        private final int size;
        private final List<Edge> edges = new ArrayList<>();
        private final List<List<Edge>> incident;

        public Graph(int size) {
            this.size = size;
            this.incident = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                incident.add(new ArrayList<>());
            }
        }

        public Graph edge(int source, int target, int weight) {
            Edge edge = new Edge(source, target, weight);
            edges.add(edge);
            incident.get(source).add(edge);
            incident.get(target).add(new Edge(target, source, weight));
            return this;
        }

        public int size() {
            return size;
        }

        List<Edge> edges() {
            return List.copyOf(edges);
        }

        List<Edge> incident(int vertex) {
            return incident.get(vertex);
        }
    }

    /**
     * Алгоритм Крускала. Рёбра перебираются по возрастанию веса; ребро берётся, если его концы
     * лежат в разных компонентах. Принадлежность компонентам проверяется системой непересекающихся
     * множеств из раздела 10. Сложность O(E log E) — её задаёт сортировка.
     */
    final class Kruskal implements SpanningTree {

        @Override
        public List<Edge> build(Graph graph, Counter counter) {
            List<Edge> sorted = new ArrayList<>(graph.edges());
            sorted.sort(Comparator.comparingInt(Edge::weight));
            DisjointSet components = new DisjointSet.Compressed(graph.size());
            List<Edge> tree = new ArrayList<>(Math.max(graph.size() - 1, 0));
            for (Edge edge : sorted) {
                counter.increment();
                if (components.union(edge.source(), edge.target(), counter)) {
                    tree.add(edge);
                }
                if (tree.size() == graph.size() - 1) {
                    break;
                }
            }
            return tree;
        }
    }

    /**
     * Алгоритм Прима. Дерево растёт от стартовой вершины: на каждом шаге берём самое лёгкое ребро,
     * выходящее из дерева наружу. Кандидаты хранятся в приоритетной очереди из раздела 9,
     * поэтому сложность O(E log V). Для несвязного графа обход перезапускается с каждой
     * непосещённой вершины, и получается остовный лес.
     */
    final class Prim implements SpanningTree {

        @Override
        public List<Edge> build(Graph graph, Counter counter) {
            boolean[] visited = new boolean[graph.size()];
            List<Edge> tree = new ArrayList<>(Math.max(graph.size() - 1, 0));
            for (int start = 0; start < graph.size(); start++) {
                if (!visited[start]) {
                    grow(graph, start, visited, tree, counter);
                }
            }
            return tree;
        }

        private void grow(Graph graph, int start, boolean[] visited, List<Edge> tree, Counter counter) {
            Queue<Edge> candidates = new PriorityQueue<>(Comparator.comparingInt(Edge::weight));
            visited[start] = true;
            candidates.addAll(graph.incident(start));
            while (!candidates.isEmpty()) {
                Edge edge = candidates.poll();
                counter.increment();
                if (visited[edge.target()]) {
                    continue;
                }
                visited[edge.target()] = true;
                tree.add(edge);
                candidates.addAll(graph.incident(edge.target()));
            }
        }
    }
}
