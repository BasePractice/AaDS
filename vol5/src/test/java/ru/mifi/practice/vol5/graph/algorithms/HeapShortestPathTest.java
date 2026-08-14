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

/** Проверка кратчайших расстояний по Дейкстре на куче с уменьшением ключа. */
@DisplayName("Кратчайшие расстояния на куче с уменьшением ключа")
final class HeapShortestPathTest {

    @DisplayName("Расстояние до исходной вершины равно нулю")
    @Test
    @Timeout(1)
    void measuresZeroDistanceToTheSource() {
        Graph<String, Integer> graph = graph("{a,b,1}{b,c,2}");
        assertThat("the source is not at distance zero",
            distances(graph).get(graph.getVertex("a")), is(0));
    }

    @DisplayName("Обход через две дуги дешевле прямой дорогой дуги")
    @Test
    @Timeout(1)
    void prefersTheCheaperDetour() {
        Graph<String, Integer> graph = graph("{a,b,1}{b,c,2}{a,c,10}");
        assertThat("the expensive direct edge wins over the cheaper detour",
            distances(graph).get(graph.getVertex("c")), is(3));
    }

    @DisplayName("Недостижимая вершина остаётся на бесконечности")
    @Test
    @Timeout(1)
    void keepsAnUnreachableVertexAtInfinity() {
        Graph<String, Integer> graph = graph("{a,b,1}{c,d,1}");
        assertThat("an unreachable vertex gets a finite distance",
            distances(graph).get(graph.getVertex("d")), is(Integer.MAX_VALUE));
    }

    @DisplayName("Уменьшает известное расстояние, а не заводит вторую запись о вершине")
    @Test
    @Timeout(1)
    void decreasesTheKnownDistance() {
        Graph<String, Integer> graph = graph("{a,b,10}{a,c,1}{c,b,2}");
        assertThat("the first found distance wins over the shorter one",
            distances(graph).get(graph.getVertex("b")), is(3));
    }

    @DisplayName("Совпадает с обычной Дейкстрой")
    @Test
    @Timeout(5)
    void agreesWithThePlainDijkstra() {
        Graph<String, Integer> graph = graph("{a,b,1}{b,c,2}{a,c,10}{c,d,1}{d,a,4}{b,d,7}");
        assertThat("the heap based search disagrees with the plain dijkstra",
            distances(graph),
            is(new DijkstraShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger())
                .distances(graph, graph.getVertex("a"))));
    }

    private static Map<Graph.Vertex<String, Integer>, Integer> distances(Graph<String, Integer> graph) {
        return new HeapShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger())
            .distances(graph, graph.getVertex("a"));
    }

    private static Graph<String, Integer> graph(String text) {
        try {
            return new ParserText<String>().parse(text, value -> value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
