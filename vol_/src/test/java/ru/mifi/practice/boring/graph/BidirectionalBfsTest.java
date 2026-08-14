package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Двунаправленный поиск в ширину")
final class BidirectionalBfsTest {

    @DisplayName("Находит путь по цепочке вершин")
    @Test
    @Timeout(1)
    void findsThePathAlongTheChain() {
        assertThat("the path along the chain is wrong",
            new BidirectionalBfs().path(chain(5), 0, 4), contains(0, 1, 2, 3, 4));
    }

    @DisplayName("Путь из вершины в саму себя состоит из неё одной")
    @Test
    @Timeout(1)
    void findsTheEmptyPathToItself() {
        assertThat("the path to itself is longer than a single vertex",
            new BidirectionalBfs().path(chain(5), 2, 2), contains(2));
    }

    @DisplayName("Берёт короткий путь, а не длинный обход")
    @Test
    @Timeout(1)
    void takesTheShortPath() {
        Graph graph = chain(5).both(0, 4, 1);
        assertThat("the long way around wins over the short path",
            new BidirectionalBfs().path(graph, 0, 4), hasSize(2));
    }

    @DisplayName("Когда пути нет, ответ пуст")
    @Test
    @Timeout(1)
    void findsNothingWhenThereIsNoPath() {
        assertThat("a path is found where there is none",
            new BidirectionalBfs().path(new Graph(4).both(0, 1, 1).both(2, 3, 1), 0, 3), is(List.of()));
    }

    @DisplayName("Идёт по дугам в их направлении")
    @Test
    @Timeout(1)
    void followsTheDirectionOfTheEdges() {
        assertThat("the search walks an edge against its direction",
            new BidirectionalBfs().path(new Graph(3).with(0, 1, 1).with(1, 2, 1), 2, 0), is(List.of()));
    }

    @DisplayName("Находит путь в направленном графе")
    @Test
    @Timeout(1)
    void findsThePathInADirectedGraph() {
        assertThat("the path in the directed graph is wrong",
            new BidirectionalBfs().path(new Graph(3).with(0, 1, 1).with(1, 2, 1), 0, 2), contains(0, 1, 2));
    }

    private static Graph chain(int size) {
        Graph graph = new Graph(size);
        for (int vertex = 0; vertex + 1 < size; vertex++) {
            graph = graph.both(vertex, vertex + 1, 1);
        }
        return graph;
    }
}
