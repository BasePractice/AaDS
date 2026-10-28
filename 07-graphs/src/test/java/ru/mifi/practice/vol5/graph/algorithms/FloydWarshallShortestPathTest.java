package ru.mifi.practice.vol5.graph.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;
import ru.mifi.practice.vol5.graph.loader.ParserText;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка кратчайших расстояний между всеми парами вершин по Флойду — Уоршеллу. */
@DisplayName("Кратчайшие расстояния между всеми парами")
final class FloydWarshallShortestPathTest {

    @DisplayName("Расстояние вершины до себя равно нулю")
    @Test
    @Timeout(1)
    void measuresZeroDistanceToItself() {
        Graph<String, Integer> graph = graph("{a,b,1}{b,c,2}");
        assertThat("a vertex is not at distance zero from itself",
            distances(graph).get(graph.getVertex("b")).get(graph.getVertex("b")), is(0));
    }

    @DisplayName("Находит обход для каждой пары, а не только для исходной вершины")
    @Test
    @Timeout(1)
    void findsTheDetourForEveryPair() {
        Graph<String, Integer> graph = graph("{a,b,1}{b,c,2}{b,d,10}{c,d,3}");
        assertThat("the detour between the inner vertices is missed",
            distances(graph).get(graph.getVertex("b")).get(graph.getVertex("d")), is(5));
    }

    @DisplayName("Недостижимая пара остаётся на бесконечности")
    @Test
    @Timeout(1)
    void keepsAnUnreachablePairAtInfinity() {
        Graph<String, Integer> graph = graph("{a,b,1}{c,d,1}");
        assertThat("an unreachable pair gets a finite distance",
            distances(graph).get(graph.getVertex("a")).get(graph.getVertex("d")), is(Integer.MAX_VALUE));
    }

    @DisplayName("Считает путь через отрицательную дугу")
    @Test
    @Timeout(1)
    void countsThePathThroughTheNegativeEdge() {
        Graph<String, Integer> graph = graph("{a,b,2}{a,c,5}{b,c,-4}");
        assertThat("the negative edge is left out of the shortest path",
            distances(graph).get(graph.getVertex("a")).get(graph.getVertex("c")), is(-2));
    }

    @DisplayName("Совпадает с Дейкстрой на положительных весах")
    @Test
    @Timeout(5)
    void agreesWithDijkstraOnPositiveWeights() {
        Graph<String, Integer> graph = graph("{a,b,1}{b,c,2}{a,c,10}{c,d,1}{d,a,4}");
        assertThat("the all pairs distances disagree with the dijkstra ones",
            distances(graph).get(graph.getVertex("a")),
            is(new DijkstraShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger())
                .distances(graph, graph.getVertex("a"))));
    }

    private static Map<Graph.Vertex<String, Integer>, Map<Graph.Vertex<String, Integer>, Integer>> distances(
        Graph<String, Integer> graph) {
        return new FloydWarshallShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger())
            .distances(graph);
    }

    private static Graph<String, Integer> graph(String text) {
        try {
            return new ParserText<String>().parse(text, value -> value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
