package ru.mifi.practice.vol5.graph.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;
import ru.mifi.practice.vol5.graph.loader.ParserText;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка кратчайших расстояний по Дейкстре. */
@DisplayName("Кратчайшие расстояния по Дейкстре")
final class DijkstraShortestPathTest {

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

    @DisplayName("Прямая дешёвая дуга выигрывает у дорогого обхода")
    @Test
    @Timeout(1)
    void prefersTheCheaperDirectEdge() {
        Graph<String, Integer> graph = graph("{a,b,10}{b,c,10}{a,c,1}");
        assertThat("the expensive detour wins over the cheaper direct edge",
            distances(graph).get(graph.getVertex("c")), is(1));
    }

    @DisplayName("Недостижимая вершина остаётся на бесконечности")
    @Test
    @Timeout(1)
    void keepsAnUnreachableVertexAtInfinity() {
        Graph<String, Integer> graph = graph("{a,b,1}{c,d,1}");
        assertThat("an unreachable vertex gets a finite distance",
            distances(graph).get(graph.getVertex("d")), is(Integer.MAX_VALUE));
    }

    @DisplayName("Дуги учитываются в направлении, в котором заданы")
    @Test
    @Timeout(1)
    void followsTheEdgeDirection() {
        Graph<String, Integer> graph = graph("{b,a,1}");
        assertThat("the search walks an edge against its direction",
            distances(graph).get(graph.getVertex("b")), is(Integer.MAX_VALUE));
    }

    private static java.util.Map<Graph.Vertex<String, Integer>, Integer> distances(Graph<String, Integer> graph) {
        return new DijkstraShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger())
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
