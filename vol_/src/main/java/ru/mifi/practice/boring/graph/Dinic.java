package ru.mifi.practice.boring.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Максимальный поток по Диницу. Простой алгоритм Форда — Фалкерсона ищет любой путь из истока в
 * сток и гонит по нему поток; беда в том, что неудачно выбранный путь заставляет потом отменять
 * себя обратными дугами, и число итераций зависит от величин ёмкостей.
 *
 * <p>Диниц наводит порядок двумя приёмами. Первый — послойная сеть: поиск в ширину размечает
 * вершины расстоянием от истока, и дальше разрешено ходить только со слоя на следующий слой.
 * Второй — блокирующий поток: в этой послойной сети поиск в глубину гонит поток, пока хоть один
 * путь есть, а исчерпанные дуги больше не просматриваются — каждая вершина помнит, до какой
 * своей дуги дошла.
 *
 * <p>После каждого блокирующего потока расстояние от истока до стока строго растёт, поэтому
 * фаз не больше n, а общая цена — O(n²·m). Ёмкости в оценку не входят вовсе.
 *
 * <p>Остаточная сеть хранится парами: у каждой дуги есть обратная нулевой ёмкости, и они лежат
 * рядом, поэтому обратную дугу находит операция «исключающее или с единицей».
 */
public final class Dinic {

    public int flow(Graph graph, int source, int sink) {
        if (source == sink) {
            return 0;
        }
        Network network = new Network(graph);
        int flow = 0;
        while (network.layered(source, sink)) {
            Arrays.fill(network.reached, 0);
            for (int pushed = network.push(source, sink, Integer.MAX_VALUE);
                 pushed > 0;
                 pushed = network.push(source, sink, Integer.MAX_VALUE)) {
                flow += pushed;
            }
        }
        return flow;
    }

    /** Остаточная сеть: дуги парами «прямая — обратная» и разметка по слоям. */
    private static final class Network {
        private final List<int[]> arcs = new ArrayList<>();
        private final List<List<Integer>> outgoing = new ArrayList<>();
        private final int[] layers;
        private final int[] reached;

        Network(Graph graph) {
            for (int vertex = 0; vertex < graph.size(); vertex++) {
                outgoing.add(new ArrayList<>());
            }
            for (Graph.Edge edge : graph.edges()) {
                add(edge.from(), edge.to(), edge.weight());
            }
            this.layers = new int[graph.size()];
            this.reached = new int[graph.size()];
        }

        void add(int from, int to, int capacity) {
            outgoing.get(from).add(arcs.size());
            arcs.add(new int[]{to, capacity});
            outgoing.get(to).add(arcs.size());
            arcs.add(new int[]{from, 0});
        }

        /** Разметка слоёв поиском в ширину; ложь, если сток стал недостижим. */
        boolean layered(int source, int sink) {
            Arrays.fill(layers, -1);
            layers[source] = 0;
            Deque<Integer> pending = new ArrayDeque<>(List.of(source));
            while (!pending.isEmpty()) {
                int vertex = pending.poll();
                for (int arc : outgoing.get(vertex)) {
                    if (arcs.get(arc)[1] > 0 && layers[arcs.get(arc)[0]] < 0) {
                        layers[arcs.get(arc)[0]] = layers[vertex] + 1;
                        pending.add(arcs.get(arc)[0]);
                    }
                }
            }
            return layers[sink] >= 0;
        }

        /** Поиск в глубину по слоям: гонит поток вперёд, пока путь есть. */
        int push(int vertex, int sink, int limit) {
            if (vertex == sink || limit == 0) {
                return limit;
            }
            while (reached[vertex] < outgoing.get(vertex).size()) {
                int arc = outgoing.get(vertex).get(reached[vertex]);
                int target = arcs.get(arc)[0];
                if (layers[target] == layers[vertex] + 1 && arcs.get(arc)[1] > 0) {
                    int pushed = push(target, sink, Math.min(limit, arcs.get(arc)[1]));
                    if (pushed > 0) {
                        arcs.get(arc)[1] -= pushed;
                        arcs.get(arc ^ 1)[1] += pushed;
                        return pushed;
                    }
                }
                reached[vertex]++;
            }
            return 0;
        }
    }
}
