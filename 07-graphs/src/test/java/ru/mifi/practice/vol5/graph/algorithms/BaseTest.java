package ru.mifi.practice.vol5.graph.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.loader.ParserText;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@DisplayName("Обходы графа")
final class BaseTest {

    @DisplayName("Поиск в глубину обходит связный граф")
    @Test
    @Timeout(1)
    void walksAConnectedGraphInDepth() {
        assertThat("depth first search dont reach every vertex of a connected graph",
            visited(graph("{a,b}{b,c}"), true), containsInAnyOrder("a", "b", "c"));
    }

    @DisplayName("Поиск в глубину обходит все компоненты связности")
    @Test
    @Timeout(1)
    void walksEveryComponentInDepth() {
        assertThat("depth first search stops at the component of the first vertex",
            visited(graph("{a,b}{c,d}"), true), containsInAnyOrder("a", "b", "c", "d"));
    }

    @DisplayName("Поиск в ширину обходит все компоненты связности")
    @Test
    @Timeout(1)
    void walksEveryComponentInBreadth() {
        assertThat("breadth first search stops at the component of the first vertex",
            visited(graph("{a,b}{c,d}"), false), containsInAnyOrder("a", "b", "c", "d"));
    }

    @DisplayName("Поиск в ширину обходит по уровням")
    @Test
    @Timeout(1)
    void walksLevelByLevelInBreadth() {
        assertThat("breadth first search dont walk level by level",
            visited(graph("{a,b}{a,c}{b,d}"), false), contains("a", "b", "c", "d"));
    }

    @DisplayName("Пустой граф обходится без ошибок")
    @Test
    @Timeout(1)
    void walksAnEmptyGraphWithoutFailing() {
        assertThat("an empty graph dont produce an empty walk", visited(graph(""), true).size(), is(0));
    }

    @DisplayName("Поиск цикла на ациклическом графе ничего не находит")
    @Test
    @Timeout(1)
    void findsNoCircleInAnAcyclicGraph() {
        assertThat("a circle is reported in an acyclic graph",
            new Base<String, Integer>().searchCircle(graph("{a,b}{b,c}")).isEmpty(), is(true));
    }

    @DisplayName("Поиск цикла находит цикл")
    @Test
    @Timeout(1)
    void findsACircle() {
        assertThat("an existing circle dont get found",
            new Base<String, Integer>().searchCircle(graph("{a,b}{b,c}{c,a}")).isEmpty(), is(false));
    }

    private static List<String> visited(Graph<String, Integer> graph, boolean depth) {
        List<String> order = new ArrayList<>();
        Base<String, Integer> base = new Base<>();
        Algorithms.Visitor<String, Integer> visitor = vertex -> order.add(vertex.value());
        if (depth) {
            base.dfs(graph, visitor);
        } else {
            base.bfs(graph, visitor);
        }
        return order;
    }

    private static Graph<String, Integer> graph(String text) {
        try {
            return new ParserText<String>().parse(text, value -> value);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось разобрать граф", e);
        }
    }
}
