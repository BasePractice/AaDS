package ru.mifi.practice.vol5.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol5.graph.loader.ParserText;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/** Проверка матрицы смежности против списков смежности того же графа. */
@DisplayName("Матрица смежности")
final class AdjacencyMatrixTest {

    @DisplayName("Отмечает дугу, которая есть в графе")
    @Test
    @Timeout(1)
    void marksAnExistingEdge() {
        Graph<String, Integer> graph = graph("{1,2,4}{2,3,1}");
        assertThat("an existing edge is missing from the matrix",
            new AdjacencyMatrix<>(graph).adjacent(graph.getVertex("1"), graph.getVertex("2")), is(true));
    }

    @DisplayName("Не путает направление дуги")
    @Test
    @Timeout(1)
    void keepsTheEdgeDirection() {
        Graph<String, Integer> graph = graph("{1,2,4}{2,3,1}");
        assertThat("a reversed edge shows up in the matrix",
            new AdjacencyMatrix<>(graph).adjacent(graph.getVertex("2"), graph.getVertex("1")), is(false));
    }

    @DisplayName("Хранит вес дуги")
    @Test
    @Timeout(1)
    void storesTheEdgeWeight() {
        Graph<String, Integer> graph = graph("{1,2,4}{2,3,7}");
        assertThat("the matrix loses the edge weight",
            new AdjacencyMatrix<>(graph).weight(graph.getVertex("2"), graph.getVertex("3")).orElseThrow(), is(7));
    }

    @DisplayName("Из параллельных дуг помнит самую лёгкую")
    @Test
    @Timeout(1)
    void keepsTheLightestOfParallelEdges() {
        Graph<String, Integer> graph = graph("{1,2,9}{1,2,3}{1,2,5}");
        assertThat("a heavier parallel edge wins over the lightest",
            new AdjacencyMatrix<>(graph).weight(graph.getVertex("1"), graph.getVertex("2")).orElseThrow(), is(3));
    }

    @DisplayName("Даёт тех же соседей, что и список смежности")
    @Test
    @Timeout(1)
    void listsTheSameNeighboursAsTheAdjacencyList() {
        Graph<String, Integer> graph = graph("{1,4,1}{1,2,1}{1,5,1}{2,3,1}");
        assertThat("matrix neighbours disagree with the adjacency list",
            labels(new AdjacencyMatrix<>(graph).neighbours(graph.getVertex("1"))), contains("2", "4", "5"));
    }

    @DisplayName("Размер равен числу вершин")
    @Test
    @Timeout(1)
    void matchesTheVertexCount() {
        assertThat("matrix side dont match the vertex count",
            new AdjacencyMatrix<>(graph("{1,2,1}{2,3,1}{3,4,1}")).size(), is(4));
    }

    private static List<String> labels(List<Graph.Vertex<String, Integer>> vertices) {
        return vertices.stream().map(Graph.Vertex::label).sorted().collect(Collectors.toList());
    }

    private static Graph<String, Integer> graph(String text) {
        try {
            return new ParserText<String>().parse(text, value -> value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
