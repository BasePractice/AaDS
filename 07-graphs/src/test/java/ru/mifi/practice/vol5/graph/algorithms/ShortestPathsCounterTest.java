package ru.mifi.practice.vol5.graph.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка согласованной единицы подсчёта и сохранения результатов поиска. */
@DisplayName("Счётчики попыток релаксации")
@Timeout(2)
final class ShortestPathsCounterTest {
    private final DijkstraShortestPath<String, Integer> dijkstra =
        new DijkstraShortestPath<>(Integer.MAX_VALUE, 0, Weight.ofInteger());
    private final BellmanFordShortestPath<String, Integer> ford =
        new BellmanFordShortestPath<>(Integer.MAX_VALUE, 0, Weight.ofInteger());
    private final FloydWarshallShortestPath<String, Integer> floyd =
        new FloydWarshallShortestPath<>(Integer.MAX_VALUE, 0, Weight.ofInteger());

    @DisplayName("Все алгоритмы возвращают одну таблицу, выполняя разное число попыток")
    @Test
    void comparesTheSameAllPairsTask() {
        Graph<String, Integer> graph = graph();
        Counter dijkstraCount = Counter.create();
        Counter fordCount = Counter.create();
        Counter floydCount = Counter.create();
        var expected = floyd.distances(graph);
        assertThat("counting changes floyd distances", floyd.distances(graph, floydCount), is(expected));
        for (var source : graph.getVertices()) {
            assertThat("counting changes dijkstra distances",
                dijkstra.distances(graph, source, dijkstraCount), is(expected.get(source)));
            assertThat("counting changes ford distances",
                ford.distances(graph, source, fordCount), is(expected.get(source)));
        }
        assertThat("unexpected dijkstra all pairs count", dijkstraCount.count(), is(4));
        assertThat("unexpected ford all pairs count", fordCount.count(), is(8));
        assertThat("unexpected floyd all pairs count", floydCount.count(), is(10));
    }

    @DisplayName("Неуспешное сравнение тоже считается, посещённая цель Дейкстры пропускается")
    @Test
    void countsUnsuccessfulAttemptsButSkipsVisitedTargets() {
        Graph<String, Integer> graph = graph();
        graph.getVertex("B").addEdge(graph.getVertex("A"), 5);
        graph.getVertex("A").addEdge(graph.getVertex("C"), 20);
        Counter counter = Counter.create();
        assertThat("counting changes the direct search", dijkstra.distances(graph, graph.getVertex("A"), counter),
            is(dijkstra.distances(graph, graph.getVertex("A"))));
        assertThat("the failed candidate or the visited target is counted incorrectly", counter.count(), is(4));
    }

    @DisplayName("Беллман — Форд считает заключительную проверку цикла")
    @Test
    void countsTheFinalNegativeCycleCheck() {
        Graph<String, Integer> graph = new Graph.Standard<>();
        var first = graph.addVertex("A", "A");
        var second = graph.addVertex("B", "B");
        first.addEdge(second, 1);
        Counter counter = Counter.create();
        assertThat("counting changes the distance", ford.distances(graph, first, counter).get(second), is(1));
        assertThat("the final cycle check is omitted", counter.count(), is(2));
        second.addEdge(first, -2);
        counter.reset();
        assertThrows(IllegalStateException.class, () -> ford.distances(graph, first, counter));
        assertThat("the failing cycle check is omitted", counter.count(), is(4));
    }

    @DisplayName("Повторные запуски накапливают число попыток, reset начинает замер заново")
    @Test
    void accumulatesUntilExplicitReset() {
        Graph<String, Integer> graph = graph();
        Counter counter = Counter.create();
        var source = graph.getVertex("A");
        dijkstra.distances(graph, source, counter);
        dijkstra.distances(graph, source, counter);
        assertThat("dijkstra resets the caller counter", counter.count(), is(6));
        counter.reset();
        ford.distances(graph, source, counter);
        ford.distances(graph, source, counter);
        assertThat("ford resets the caller counter", counter.count(), is(12));
        counter.reset();
        floyd.distances(graph, counter);
        floyd.distances(graph, counter);
        assertThat("floyd resets the caller counter", counter.count(), is(20));
        counter.reset();
        floyd.distances(graph, counter);
        assertThat("the explicit reset did not start a fresh measurement", counter.count(), is(10));
    }

    @DisplayName("Недостижимые компоненты не добавляют попыток от исходной вершины")
    @Test
    void skipsUnreachableEdgesAndPairs() {
        Graph<String, Integer> graph = graph();
        var isolated = graph.addVertex("D", "D");
        isolated.addEdge(isolated, 1);
        Counter counter = Counter.create();
        assertThat("dijkstra makes an unreachable vertex finite",
            dijkstra.distances(graph, graph.getVertex("A"), counter).get(isolated), is(Integer.MAX_VALUE));
        assertThat("dijkstra counts unreachable edges", counter.count(), is(3));
        counter.reset();
        assertThat("ford makes an unreachable vertex finite",
            ford.distances(graph, graph.getVertex("A"), counter).get(isolated), is(Integer.MAX_VALUE));
        assertThat("ford counts unreachable edges", counter.count(), is(6));
        counter.reset();
        assertThat("floyd makes an unreachable pair finite",
            floyd.distances(graph, counter).get(graph.getVertex("A")).get(isolated), is(Integer.MAX_VALUE));
        assertThat("floyd counts unreachable pairs", counter.count(), is(11));
    }

    @DisplayName("Пустая таблица Флойда не требует попыток релаксации")
    @Test
    void countsNothingForAnEmptyGraph() {
        Counter counter = Counter.create();
        assertThat("the empty graph has distances", floyd.distances(new Graph.Standard<>(), counter).isEmpty(), is(true));
        assertThat("initialization is counted", counter.count(), is(0));
    }

    private static Graph<String, Integer> graph() {
        Graph<String, Integer> graph = new Graph.Standard<>();
        var first = graph.addVertex("A", "A");
        var second = graph.addVertex("B", "B");
        var third = graph.addVertex("C", "C");
        first.addEdge(second, 1);
        first.addEdge(third, 10);
        second.addEdge(third, 2);
        return graph;
    }
}
