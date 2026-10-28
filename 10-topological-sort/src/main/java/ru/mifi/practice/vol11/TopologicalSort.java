package ru.mifi.practice.vol11;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Топологическая сортировка из раздела 11 курса: линейный порядок вершин ориентированного графа,
 * при котором каждое ребро ведёт от более раннего к более позднему. Существует только для графа
 * без циклов, поэтому обе реализации возвращают пустой результат, если цикл найден.
 */
public interface TopologicalSort {

    /**
     * Порядок вершин или пустое значение, если в графе есть цикл.
     */
    Optional<List<Integer>> sort(Graph graph, Counter counter);

    /**
     * Поиск самого цикла, который делает порядок невозможным, а не только факта, что он есть.
     */
    interface Cycle {

        /**
         * Вершины цикла в порядке обхода: из каждой есть ребро в следующую, из последней — в первую.
         * Пустой список, если циклов нет.
         */
        List<Integer> cycle(Graph graph, Counter counter);
    }

    /**
     * Ориентированный граф списками смежности.
     */
    final class Graph {
        private final List<List<Integer>> targets;

        public Graph(int size) {
            this.targets = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                targets.add(new ArrayList<>());
            }
        }

        public Graph edge(int source, int target) {
            targets.get(source).add(target);
            return this;
        }

        public int size() {
            return targets.size();
        }

        List<Integer> targets(int vertex) {
            return targets.get(vertex);
        }
    }

    /**
     * Алгоритм Кана. Считаем входящие степени, кладём в очередь вершины без зависимостей и
     * снимаем их по одной, уменьшая степень соседей. Если в результат попали не все вершины,
     * оставшиеся заперты циклом. Сложность O(V + E).
     */
    final class Kahn implements TopologicalSort {

        @Override
        public Optional<List<Integer>> sort(Graph graph, Counter counter) {
            int size = graph.size();
            int[] incoming = new int[size];
            for (int vertex = 0; vertex < size; vertex++) {
                for (int target : graph.targets(vertex)) {
                    counter.increment();
                    ++incoming[target];
                }
            }
            Deque<Integer> ready = new ArrayDeque<>();
            for (int vertex = 0; vertex < size; vertex++) {
                if (incoming[vertex] == 0) {
                    ready.add(vertex);
                }
            }
            List<Integer> order = new ArrayList<>(size);
            while (!ready.isEmpty()) {
                int vertex = ready.poll();
                order.add(vertex);
                for (int target : graph.targets(vertex)) {
                    counter.increment();
                    --incoming[target];
                    if (incoming[target] == 0) {
                        ready.add(target);
                    }
                }
            }
            if (order.size() != size) {
                return Optional.empty();
            }
            return Optional.of(order);
        }
    }

    /**
     * Обход в глубину с трёхцветной покраской. Вершина добавляется в результат при выходе из неё,
     * то есть после всех потомков, поэтому итог нужно развернуть. Встреча серой вершины означает
     * обратное ребро, то есть цикл. Сложность O(V + E).
     *
     * <p>Чтобы показать сам цикл, обход запоминает, из какой вершины пришёл в каждую. Обратное
     * ребро из vertex в серую target замыкает цикл: от vertex по этим ссылкам поднимаемся до target,
     * и пройденные вершины в обратном порядке и есть цикл.
     */
    final class DepthFirst implements TopologicalSort, Cycle {
        private static final int WHITE = 0;
        private static final int GRAY = 1;
        private static final int BLACK = 2;

        @Override
        public Optional<List<Integer>> sort(Graph graph, Counter counter) {
            int size = graph.size();
            int[] colors = new int[size];
            List<Integer> order = new ArrayList<>(size);
            for (int vertex = 0; vertex < size; vertex++) {
                if (colors[vertex] == WHITE && !visit(graph, vertex, colors, order, counter)) {
                    return Optional.empty();
                }
            }
            List<Integer> reversed = new ArrayList<>(order.size());
            for (int i = order.size() - 1; i >= 0; i--) {
                reversed.add(order.get(i));
            }
            return Optional.of(reversed);
        }

        private boolean visit(Graph graph, int vertex, int[] colors, List<Integer> order, Counter counter) {
            colors[vertex] = GRAY;
            for (int target : graph.targets(vertex)) {
                counter.increment();
                if (colors[target] == GRAY) {
                    return false;
                }
                if (colors[target] == WHITE && !visit(graph, target, colors, order, counter)) {
                    return false;
                }
            }
            colors[vertex] = BLACK;
            order.add(vertex);
            return true;
        }

        @Override
        public List<Integer> cycle(Graph graph, Counter counter) {
            int[] colors = new int[graph.size()];
            int[] parents = new int[graph.size()];
            for (int vertex = 0; vertex < graph.size(); vertex++) {
                if (colors[vertex] == WHITE) {
                    List<Integer> cycle = trace(graph, vertex, colors, parents, counter);
                    if (!cycle.isEmpty()) {
                        return cycle;
                    }
                }
            }
            return List.of();
        }

        private List<Integer> trace(Graph graph, int vertex, int[] colors, int[] parents, Counter counter) {
            colors[vertex] = GRAY;
            for (int target : graph.targets(vertex)) {
                counter.increment();
                if (colors[target] == GRAY) {
                    return unwind(target, vertex, parents);
                }
                if (colors[target] == WHITE) {
                    parents[target] = vertex;
                    List<Integer> cycle = trace(graph, target, colors, parents, counter);
                    if (!cycle.isEmpty()) {
                        return cycle;
                    }
                }
            }
            colors[vertex] = BLACK;
            return List.of();
        }

        private List<Integer> unwind(int start, int end, int[] parents) {
            Deque<Integer> cycle = new ArrayDeque<>();
            for (int vertex = end; vertex != start; vertex = parents[vertex]) {
                cycle.addFirst(vertex);
            }
            cycle.addFirst(start);
            return new ArrayList<>(cycle);
        }
    }
}
