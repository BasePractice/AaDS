package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Остовное дерево по Борувке")
final class BoruvkaTest {

    @DisplayName("Дерево связывает все вершины")
    @Test
    @Timeout(1)
    void connectsEveryVertex() {
        assertThat("the spanning tree misses a vertex",
            new Boruvka().tree(square()), hasSize(3));
    }

    @DisplayName("Дерево берёт наименьший общий вес")
    @Test
    @Timeout(1)
    void takesTheLightestTotalWeight() {
        assertThat("the spanning tree is heavier than it should be", weight(new Boruvka().tree(square())), is(6));
    }

    @DisplayName("Тяжёлое ребро в дерево не попадает")
    @Test
    @Timeout(1)
    void leavesTheHeaviestEdgeOut() {
        assertThat("the heaviest edge got into the tree",
            new Boruvka().tree(square()).stream().anyMatch(edge -> edge.weight() == 10), is(false));
    }

    @DisplayName("В несвязном графе получается лес, а не дерево")
    @Test
    @Timeout(1)
    void buildsAForestOnADisconnectedGraph() {
        assertThat("the disconnected graph got a full spanning tree",
            new Boruvka().tree(new Graph(4).both(0, 1, 1).both(2, 3, 1)), hasSize(2));
    }

    @DisplayName("Граф без рёбер даёт пустое дерево")
    @Test
    @Timeout(1)
    void buildsNothingWithoutEdges() {
        assertThat("the graph without edges got a tree", new Boruvka().tree(new Graph(3)), is(List.of()));
    }

    private static Graph square() {
        return new Graph(4).both(0, 1, 1).both(1, 2, 2).both(2, 3, 3).both(3, 0, 10);
    }

    private static int weight(List<Graph.Edge> tree) {
        return tree.stream().mapToInt(Graph.Edge::weight).sum();
    }
}
