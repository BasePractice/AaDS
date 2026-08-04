package ru.mifi.practice.vol11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Проверка обоих порядков на случайных бесконтурных графах.
 *
 * <p>Порядков у графа обычно много, поэтому сравнивать сами списки бессмысленно — проверяется
 * определяющее свойство: каждая дуга ведёт вперёд по полученному порядку. Бесконтурность графа
 * обеспечивается тем, что дуги проводятся только от меньшего номера к большему.
 */
@DisplayName("Топологическая сортировка на случайных графах")
final class TopologicalSortRandomTest {

    @DisplayName("Порядок Кана ведёт все дуги вперёд")
    @Test
    @Timeout(10)
    void keepsEveryEdgeForwardWithKahn() {
        assertThat("Kahn ordering turns an edge backwards", violations(new TopologicalSort.Kahn()), is(0));
    }

    @DisplayName("Порядок обхода в глубину ведёт все дуги вперёд")
    @Test
    @Timeout(10)
    void keepsEveryEdgeForwardWithDepthFirst() {
        assertThat("depth first ordering turns an edge backwards",
            violations(new TopologicalSort.DepthFirst()), is(0));
    }

    @DisplayName("Граф с контуром не сортируется")
    @Test
    @Timeout(10)
    void refusesAGraphWithACycle() {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(3);
        graph.edge(0, 1).edge(1, 2).edge(2, 0);
        assertThat("a graph with a cycle gets an ordering",
            new TopologicalSort.Kahn().sort(graph, Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Граф с контуром не сортируется и обходом в глубину")
    @Test
    @Timeout(10)
    void refusesAGraphWithACycleWhenWalkingInDepth() {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(3);
        graph.edge(0, 1).edge(1, 2).edge(2, 0);
        assertThat("a graph with a cycle gets an ordering from the depth first walk",
            new TopologicalSort.DepthFirst().sort(graph, Counter.create()).isPresent(), is(false));
    }

    private static int violations(TopologicalSort sort) {
        Random random = new Random(20260804L);
        int broken = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            int size = 2 + random.nextInt(12);
            List<int[]> edges = new ArrayList<>();
            TopologicalSort.Graph graph = new TopologicalSort.Graph(size);
            for (int extra = 0; extra < size * 2; extra++) {
                int source = random.nextInt(size);
                int target = random.nextInt(size);
                if (source < target) {
                    graph.edge(source, target);
                    edges.add(new int[]{source, target});
                }
            }
            List<Integer> order = sort.sort(graph, Counter.create()).orElseThrow();
            for (int[] edge : edges) {
                if (order.indexOf(edge[0]) > order.indexOf(edge[1])) {
                    ++broken;
                }
            }
        }
        return broken;
    }
}
