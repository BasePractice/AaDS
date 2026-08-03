package ru.mifi.practice.vol12;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.commons.Counter;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Остовное дерево")
final class SpanningTreeTest {

    static Stream<SpanningTree> implementations() {
        return Stream.of(new SpanningTree.Kruskal(), new SpanningTree.Prim());
    }

    @DisplayName("Дерево содержит на одно ребро меньше, чем вершин")
    @ParameterizedTest
    @MethodSource("implementations")
    void takesOneEdgeLessThanVertices(SpanningTree tree) {
        assertThat("the tree dont have exactly one edge less than vertices",
            tree.build(square(), Counter.create()).size(), is(3));
    }

    @DisplayName("Выбирает минимальную сумму весов")
    @ParameterizedTest
    @MethodSource("implementations")
    void minimisesTheTotalWeight(SpanningTree tree) {
        assertThat("the tree dont minimise the total weight",
            weight(tree.build(square(), Counter.create())), is(6));
    }

    @DisplayName("Тяжёлое ребро в дерево не попадает")
    @ParameterizedTest
    @MethodSource("implementations")
    void skipsTheHeaviestEdge(SpanningTree tree) {
        assertThat("the heaviest edge gets into the tree",
            tree.build(square(), Counter.create()).stream().anyMatch(e -> e.weight() == 10), is(false));
    }

    @DisplayName("Одна вершина даёт дерево без рёбер")
    @ParameterizedTest
    @MethodSource("implementations")
    void buildsAnEmptyTreeForASingleVertex(SpanningTree tree) {
        assertThat("a single vertex graph produces edges",
            tree.build(new SpanningTree.Graph(1), Counter.create()).size(), is(0));
    }

    @DisplayName("Несвязный граф даёт остовный лес")
    @ParameterizedTest
    @MethodSource("implementations")
    void buildsAForestForADisconnectedGraph(SpanningTree tree) {
        SpanningTree.Graph graph = new SpanningTree.Graph(4).edge(0, 1, 1).edge(2, 3, 1);
        assertThat("a disconnected graph dont produce a forest of two edges",
            tree.build(graph, Counter.create()).size(), is(2));
    }

    @DisplayName("Кратное ребро не создаёт цикла")
    @ParameterizedTest
    @MethodSource("implementations")
    void ignoresParallelEdges(SpanningTree tree) {
        SpanningTree.Graph graph = new SpanningTree.Graph(2).edge(0, 1, 5).edge(0, 1, 1);
        assertThat("a parallel edge closes a cycle",
            tree.build(graph, Counter.create()).size(), is(1));
    }

    @DisplayName("Из кратных рёбер берётся лёгкое")
    @ParameterizedTest
    @MethodSource("implementations")
    void takesTheLighterOfParallelEdges(SpanningTree tree) {
        SpanningTree.Graph graph = new SpanningTree.Graph(2).edge(0, 1, 5).edge(0, 1, 1);
        assertThat("the heavier of two parallel edges is taken",
            weight(tree.build(graph, Counter.create())), is(1));
    }

    /**
     * Квадрат с диагональю: лёгкий контур весит 1 + 2 + 3, диагональ весом 10 избыточна.
     */
    private static SpanningTree.Graph square() {
        return new SpanningTree.Graph(4)
            .edge(0, 1, 1)
            .edge(1, 2, 2)
            .edge(2, 3, 3)
            .edge(3, 0, 4)
            .edge(0, 2, 10);
    }

    private static int weight(List<SpanningTree.Edge> edges) {
        return edges.stream().mapToInt(SpanningTree.Edge::weight).sum();
    }
}
