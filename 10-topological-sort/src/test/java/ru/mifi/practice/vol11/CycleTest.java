package ru.mifi.practice.vol11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@DisplayName("Вывод цикла в топологической сортировке")
final class CycleTest {

    @DisplayName("Выводит вершины треугольника в порядке рёбер")
    @Test
    @Timeout(1)
    void printsTheTriangleInEdgeOrder() {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(3).edge(0, 1).edge(1, 2).edge(2, 0);
        assertThat("the cycle dont follow its edges",
            new TopologicalSort.DepthFirst().cycle(graph, Counter.create()), contains(0, 1, 2));
    }

    @DisplayName("Ациклический граф даёт пустой цикл")
    @Test
    @Timeout(1)
    void findsNoCycleInAnAcyclicGraph() {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(4).edge(0, 1).edge(0, 2).edge(1, 3).edge(2, 3);
        assertThat("an acyclic graph reports a cycle",
            new TopologicalSort.DepthFirst().cycle(graph, Counter.create()), is(empty()));
    }

    @DisplayName("Петля выводится одной вершиной")
    @Test
    @Timeout(1)
    void printsASelfLoopAsOneVertex() {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(2).edge(0, 1).edge(1, 1);
        assertThat("a self loop dont come out as a single vertex",
            new TopologicalSort.DepthFirst().cycle(graph, Counter.create()), contains(1));
    }

    @DisplayName("Не включает в цикл хвост, который к нему ведёт")
    @Test
    @Timeout(1)
    void leavesTheLeadingTailOutOfTheCycle() {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(4).edge(0, 1).edge(1, 2).edge(2, 3).edge(3, 1);
        assertThat("the path leading into the cycle is reported as part of it",
            new TopologicalSort.DepthFirst().cycle(graph, Counter.create()), contains(1, 2, 3));
    }

    @DisplayName("Каждая пара соседних вершин цикла соединена ребром")
    @Test
    @Timeout(1)
    void connectsEveryConsecutivePairOfTheCycle() {
        TopologicalSort.Graph graph = new TopologicalSort.Graph(6)
            .edge(0, 1).edge(1, 2).edge(2, 5).edge(5, 4).edge(4, 3).edge(3, 2);
        List<Integer> cycle = new TopologicalSort.DepthFirst().cycle(graph, Counter.create());
        boolean connected = true;
        for (int i = 0; i < cycle.size(); i++) {
            connected &= graph.targets(cycle.get(i)).contains(cycle.get((i + 1) % cycle.size()));
        }
        assertThat("the printed cycle skips an edge", connected, is(true));
    }
}
