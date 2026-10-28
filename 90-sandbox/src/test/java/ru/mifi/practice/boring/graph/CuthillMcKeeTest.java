package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Перестановка Катхилла — Макки")
final class CuthillMcKeeTest {

    @DisplayName("Нумерует каждую вершину ровно раз")
    @Test
    @Timeout(1)
    void numbersEveryVertexOnce() {
        assertThat("a vertex is numbered twice or lost", new CuthillMcKee().order(chain(6)).length, is(6));
    }

    @DisplayName("Сужает ленту по сравнению с исходной нумерацией")
    @Test
    @Timeout(1)
    void narrowsTheBandwidth() {
        CuthillMcKee order = new CuthillMcKee();
        Graph graph = scattered();
        assertThat("the permutation dont narrow the bandwidth",
            order.bandwidth(graph, order.order(graph)), lessThan(order.bandwidth(graph, straight(graph.size()))));
    }

    @DisplayName("Обратный порядок держит ту же ширину ленты")
    @Test
    @Timeout(1)
    void keepsTheBandwidthWhenReversed() {
        CuthillMcKee order = new CuthillMcKee();
        Graph graph = scattered();
        assertThat("the reversed order changes the bandwidth",
            order.bandwidth(graph, order.reversed(graph)), is(order.bandwidth(graph, order.order(graph))));
    }

    @DisplayName("На цепочке лента шириной в одну дугу")
    @Test
    @Timeout(1)
    void keepsTheChainBandwidthAtOne() {
        CuthillMcKee order = new CuthillMcKee();
        assertThat("the chain got a wider band than a single step",
            order.bandwidth(chain(6), order.order(chain(6))), is(1));
    }

    @DisplayName("Нумерует и несвязный граф целиком")
    @Test
    @Timeout(1)
    void numbersADisconnectedGraph() {
        assertThat("the second component is left unnumbered",
            new CuthillMcKee().order(new Graph(4).both(0, 1, 1).both(2, 3, 1)).length, is(4));
    }

    private static Graph chain(int size) {
        Graph graph = new Graph(size);
        for (int vertex = 0; vertex + 1 < size; vertex++) {
            graph = graph.both(vertex, vertex + 1, 1);
        }
        return graph;
    }

    /** Цепочка, вершины которой пронумерованы вразнобой: лента исходно широкая. */
    private static Graph scattered() {
        return new Graph(7)
            .both(0, 6, 1).both(6, 1, 1).both(1, 5, 1).both(5, 2, 1).both(2, 4, 1).both(4, 3, 1);
    }

    private static int[] straight(int size) {
        int[] order = new int[size];
        for (int vertex = 0; vertex < size; vertex++) {
            order[vertex] = vertex;
        }
        return order;
    }
}
