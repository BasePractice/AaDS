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
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка кратчайших расстояний по Беллману — Форду, включая отрицательные веса. */
@DisplayName("Кратчайшие расстояния по Беллману — Форду")
final class BellmanFordShortestPathTest {

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

    @DisplayName("Считает путь через отрицательную дугу, на которой ошибается Дейкстра")
    @Test
    @Timeout(1)
    void countsThePathThroughTheNegativeEdge() {
        Graph<String, Integer> graph = graph("{a,b,2}{a,c,5}{b,c,-4}");
        assertThat("the negative edge is left out of the shortest path",
            distances(graph).get(graph.getVertex("c")), is(-2));
    }

    @DisplayName("Недостижимая вершина остаётся на бесконечности")
    @Test
    @Timeout(1)
    void keepsAnUnreachableVertexAtInfinity() {
        Graph<String, Integer> graph = graph("{a,b,1}{c,d,1}");
        assertThat("an unreachable vertex gets a finite distance",
            distances(graph).get(graph.getVertex("d")), is(Integer.MAX_VALUE));
    }

    @DisplayName("Отказывается считать граф с отрицательным циклом")
    @Test
    @Timeout(1)
    void refusesTheNegativeCycle() {
        Graph<String, Integer> graph = graph("{a,b,1}{b,c,-1}{c,b,-1}");
        assertThat("the negative cycle passes for a shortest path",
            assertThrows(IllegalStateException.class, () -> distances(graph)).getMessage(),
            is("Negative cycle leaves the shortest path undefined"));
    }

    private static Map<Graph.Vertex<String, Integer>, Integer> distances(Graph<String, Integer> graph) {
        return new BellmanFordShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger())
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
