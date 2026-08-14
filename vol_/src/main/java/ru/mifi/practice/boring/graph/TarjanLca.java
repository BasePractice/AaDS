package ru.mifi.practice.boring.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Наименьший общий предок для набора запросов — офлайн-алгоритм Тарьяна. Офлайн значит, что все
 * запросы известны заранее: это и позволяет ответить на них за один обход дерева вместо
 * предобработки двоичными подъёмами.
 *
 * <p>Обход в глубину, выходя из поддерева, приклеивает его к родителю системой непересекающихся
 * множеств, а представителем объединённого множества назначает родителя. Тогда в момент, когда
 * обход заканчивает вершину u, представитель множества любой уже обойдённой вершины v — это
 * ровно наименьший общий предок u и v. Остаётся при выходе из u просмотреть её запросы и взять
 * представителя для тех, чей второй конец уже пройден.
 *
 * <p>Цена — один обход и почти линейная работа системы множеств.
 */
public final class TarjanLca {

    /** Ответы на запросы: для каждой пары вершин — их наименьший общий предок. */
    public Map<Query, Integer> ancestors(Graph tree, int root, List<Query> queries) {
        Search search = new Search(tree, queries);
        search.walk(root, root);
        return search.answers;
    }

    /** Запрос: две вершины, для которых нужен общий предок. */
    public record Query(int left, int right) {
    }

    /** Состояние обхода: система множеств, отметки о пройденном и накопленные ответы. */
    private static final class Search {
        private final Graph tree;
        private final Map<Query, Integer> answers = new HashMap<>();
        private final List<Query> queries;
        private final State state;

        Search(Graph tree, List<Query> queries) {
            this.tree = tree;
            this.queries = queries;
            this.state = new State(tree.size());
        }

        void walk(int vertex, int parent) {
            state.visited[vertex] = true;
            for (Graph.Edge edge : tree.edges(vertex)) {
                if (!state.visited[edge.to()]) {
                    walk(edge.to(), vertex);
                    state.components[state.root(edge.to())] = vertex;
                }
            }
            state.done[vertex] = true;
            answer(vertex);
        }

        /** Запросы, второй конец которых уже пройден, закрываются представителем множества. */
        private void answer(int vertex) {
            for (Query query : queries) {
                if (query.left() == vertex && state.done[query.right()]) {
                    answers.put(query, state.root(query.right()));
                }
                if (query.right() == vertex && state.done[query.left()]) {
                    answers.put(query, state.root(query.left()));
                }
            }
        }
    }

    /** Система непересекающихся множеств вместе с отметками обхода. */
    private static final class State {
        private final int[] components;
        private final boolean[] visited;
        private final boolean[] done;

        State(int size) {
            this.components = new int[size];
            this.visited = new boolean[size];
            this.done = new boolean[size];
            for (int vertex = 0; vertex < size; vertex++) {
                components[vertex] = vertex;
            }
        }

        int root(int vertex) {
            int root = vertex;
            while (components[root] != root) {
                root = components[root];
            }
            components[vertex] = root;
            return root;
        }
    }
}
