package ru.mifi.practice.vol11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.commons.Counter;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Топологическая сортировка")
final class TopologicalSortTest {

    static Stream<TopologicalSort> implementations() {
        return Stream.of(new TopologicalSort.Kahn(), new TopologicalSort.DepthFirst());
    }

    @DisplayName("Ставит зависимость раньше зависящего")
    @ParameterizedTest
    @MethodSource("implementations")
    void putsDependencyBeforeDependent(TopologicalSort sort) {
        List<Integer> order = sort.sort(chain(), Counter.create()).orElseThrow();
        assertThat("dependency comes after the dependent", order.indexOf(0) < order.indexOf(2), is(true));
    }

    @DisplayName("Упорядочивает цепочку целиком")
    @ParameterizedTest
    @MethodSource("implementations")
    void ordersTheWholeChain(TopologicalSort sort) {
        assertThat("a plain chain dont come out in order",
            sort.sort(chain(), Counter.create()).orElseThrow(), contains(0, 1, 2));
    }

    @DisplayName("Возвращает все вершины, включая изолированные")
    @ParameterizedTest
    @MethodSource("implementations")
    void returnsEveryVertex(TopologicalSort sort) {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(4).edge(0, 1);
        assertThat("isolated vertices are dropped from the order",
            sort.sort(graph, Counter.create()).orElseThrow().size(), is(4));
    }

    @DisplayName("Соблюдает порядок при ветвлении")
    @ParameterizedTest
    @MethodSource("implementations")
    void respectsOrderOnABranchingGraph(TopologicalSort sort) {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(4).edge(0, 1).edge(0, 2).edge(1, 3).edge(2, 3);
        List<Integer> order = sort.sort(graph, Counter.create()).orElseThrow();
        assertThat("a branch ends before its join", order.indexOf(1) < order.indexOf(3), is(true));
    }

    @DisplayName("Цикл делает порядок невозможным")
    @ParameterizedTest
    @MethodSource("implementations")
    void reportsACycle(TopologicalSort sort) {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(3).edge(0, 1).edge(1, 2).edge(2, 0);
        assertThat("a cyclic graph gets a topological order",
            sort.sort(graph, Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Петля тоже цикл")
    @ParameterizedTest
    @MethodSource("implementations")
    void reportsASelfLoop(TopologicalSort sort) {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(2).edge(0, 0);
        assertThat("a self loop gets a topological order",
            sort.sort(graph, Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Пустой граф упорядочивается пустым списком")
    @ParameterizedTest
    @MethodSource("implementations")
    void ordersAnEmptyGraph(TopologicalSort sort) {
        assertThat("an empty graph dont produce an empty order",
            sort.sort(new TopologicalSort.Graph(0), Counter.create()).orElseThrow().size(), is(0));
    }

    private static TopologicalSort.Graph chain() {
        return new TopologicalSort.Graph(3).edge(0, 1).edge(1, 2);
    }
}
