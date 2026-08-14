package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Граф")
final class GraphTest {

    @DisplayName("Направленная дуга ведёт только в одну сторону")
    @Test
    @Timeout(1)
    void keepsTheDirectionOfTheEdge() {
        assertThat("the directed edge is walkable backwards",
            new Graph(2).with(0, 1, 5).edges(1), hasSize(0));
    }

    @DisplayName("Неориентированное ребро ведёт в обе стороны")
    @Test
    @Timeout(1)
    void walksTheUndirectedEdgeBothWays() {
        assertThat("the undirected edge is one way",
            new Graph(2).both(0, 1, 5).edges(1), hasSize(1));
    }

    @DisplayName("Добавление дуги не меняет исходный граф")
    @Test
    @Timeout(1)
    void keepsTheSourceGraphIntact() {
        Graph graph = new Graph(2);
        graph.with(0, 1, 5);
        assertThat("the source graph got the edge", graph.edges(), hasSize(0));
    }

    @DisplayName("Степень вершины — число выходящих дуг")
    @Test
    @Timeout(1)
    void countsTheDegreeOfTheVertex() {
        assertThat("the degree of the vertex is wrong",
            new Graph(3).both(0, 1, 1).both(0, 2, 1).degree(0), is(2));
    }

    @DisplayName("Вес дуги хранится вместе с ней")
    @Test
    @Timeout(1)
    void keepsTheWeightOfTheEdge() {
        assertThat("the weight of the edge is lost",
            new Graph(2).with(0, 1, 7).edges(0).get(0).weight(), is(7));
    }
}
