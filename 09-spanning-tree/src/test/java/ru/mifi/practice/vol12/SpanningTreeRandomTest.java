package ru.mifi.practice.vol12;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка Крускала и Прима между собой на случайных связных графах.
 *
 * <p>Сами остовы могут отличаться, если в графе есть рёбра равного веса, а вот суммарный вес
 * обязан совпадать: он у минимального остова единственный. На это и смотрим.
 */
@DisplayName("Остовные деревья на случайных графах")
final class SpanningTreeRandomTest {

    @DisplayName("Крускал и Прим дают остовы одного веса")
    @Test
    @Timeout(10)
    void agreeOnTheWeightOfTheSpanningTree() {
        Random random = new Random(20260804L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            SpanningTree.Graph graph = connected(random, 2 + random.nextInt(12));
            if (weight(new SpanningTree.Kruskal().build(graph, Counter.create()))
                != weight(new SpanningTree.Prim().build(graph, Counter.create()))) {
                ++mismatches;
            }
        }
        assertThat("Kruskal and Prim disagree on the weight of the spanning tree", mismatches, is(0));
    }

    @DisplayName("Остов связного графа держит на одно ребро меньше, чем вершин")
    @Test
    @Timeout(10)
    void holdsOneEdgeLessThanVertices() {
        Random random = new Random(1L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            int size = 2 + random.nextInt(12);
            if (new SpanningTree.Kruskal().build(connected(random, size), Counter.create()).size() != size - 1) {
                ++mismatches;
            }
        }
        assertThat("the spanning tree of a connected graph dont hold one edge less than vertices",
            mismatches, is(0));
    }

    private static int weight(List<SpanningTree.Edge> edges) {
        return edges.stream().mapToInt(SpanningTree.Edge::weight).sum();
    }

    /** Сначала цепочка, чтобы граф был связным, затем случайные хорды поверх неё. */
    private static SpanningTree.Graph connected(Random random, int size) {
        SpanningTree.Graph graph = new SpanningTree.Graph(size);
        for (int vertex = 1; vertex < size; vertex++) {
            graph.edge(vertex - 1, vertex, 1 + random.nextInt(20));
        }
        for (int extra = 0; extra < size; extra++) {
            int source = random.nextInt(size);
            int target = random.nextInt(size);
            if (source != target) {
                graph.edge(source, target, 1 + random.nextInt(20));
            }
        }
        return graph;
    }
}
