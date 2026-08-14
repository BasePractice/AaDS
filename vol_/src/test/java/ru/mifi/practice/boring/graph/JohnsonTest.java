package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Кратчайшие пути между всеми парами по Джонсону")
final class JohnsonTest {

    @DisplayName("Путь вершины до себя ничего не стоит")
    @Test
    @Timeout(1)
    void measuresZeroDistanceToItself() {
        int[][] distances = new Johnson().distances(triangle());
        assertThat("a vertex is not at distance zero from itself", distances[1][1], is(0));
    }

    @DisplayName("Обход дешевле прямой дорогой дуги")
    @Test
    @Timeout(1)
    void prefersTheCheaperDetour() {
        int[][] distances = new Johnson().distances(triangle());
        assertThat("the expensive direct edge wins over the cheaper detour", distances[0][2], is(3));
    }

    @DisplayName("Считает путь через отрицательную дугу")
    @Test
    @Timeout(1)
    void countsThePathThroughTheNegativeEdge() {
        int[][] distances = new Johnson().distances(new Graph(3).with(0, 1, 2).with(0, 2, 5).with(1, 2, -4));
        assertThat("the negative edge is left out of the shortest path", distances[0][2], is(-2));
    }

    @DisplayName("Недостижимая пара остаётся на бесконечности")
    @Test
    @Timeout(1)
    void keepsAnUnreachablePairAtInfinity() {
        int[][] distances = new Johnson().distances(new Graph(4).with(0, 1, 1).with(2, 3, 1));
        assertThat("an unreachable pair gets a finite distance", distances[0][3], is(Integer.MAX_VALUE));
    }

    @DisplayName("Считает пути из каждой вершины, а не только из первой")
    @Test
    @Timeout(1)
    void countsThePathsFromEveryVertex() {
        int[][] distances = new Johnson().distances(triangle());
        assertThat("the paths from the inner vertex are wrong", distances[1][2], is(2));
    }

    @DisplayName("Отрицательный цикл отвергается сразу")
    @Test
    @Timeout(1)
    void refusesTheNegativeCycle() {
        Graph graph = new Graph(3).with(0, 1, 1).with(1, 2, -1).with(2, 1, -1);
        assertThat("the negative cycle passes for a shortest path",
            assertThrows(IllegalStateException.class, () -> new Johnson().distances(graph)).getMessage(),
            is("Negative cycle leaves the shortest path undefined"));
    }

    private static Graph triangle() {
        return new Graph(3).with(0, 1, 1).with(1, 2, 2).with(0, 2, 10);
    }
}
