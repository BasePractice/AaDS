package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Эйлеров цикл по Хирхольцеру")
final class HierholzerTest {

    @DisplayName("Цикл проходит по каждому ребру")
    @Test
    @Timeout(1)
    void walksEveryEdge() {
        assertThat("the circuit misses an edge", new Hierholzer().circuit(triangle()), hasSize(4));
    }

    @DisplayName("Цикл возвращается в начало")
    @Test
    @Timeout(1)
    void returnsToTheStart() {
        List<Integer> circuit = new Hierholzer().circuit(triangle());
        assertThat("the circuit dont return to its start",
            circuit.get(0), is(circuit.get(circuit.size() - 1)));
    }

    @DisplayName("Ни одно ребро не проходится дважды")
    @Test
    @Timeout(1)
    void walksNoEdgeTwice() {
        assertThat("an edge is walked twice", repeats(new Hierholzer().circuit(triangle())), is(0));
    }

    @DisplayName("У графа с нечётной степенью цикла нет")
    @Test
    @Timeout(1)
    void findsNoCircuitWithAnOddDegree() {
        assertThat("a graph with an odd degree got a circuit",
            new Hierholzer().circuit(new Graph(3).both(0, 1, 1).both(1, 2, 1)), is(List.of()));
    }

    @DisplayName("У несвязного графа цикла нет")
    @Test
    @Timeout(1)
    void findsNoCircuitInADisconnectedGraph() {
        Graph graph = triangle().both(3, 4, 1).both(4, 5, 1).both(5, 3, 1);
        assertThat("a disconnected graph got a single circuit",
            new Hierholzer().circuit(graph), is(List.of()));
    }

    @DisplayName("Проходит восьмёрку из двух циклов")
    @Test
    @Timeout(1)
    void walksTheFigureOfEight() {
        Graph graph = new Graph(5)
            .both(0, 1, 1).both(1, 2, 1).both(2, 0, 1)
            .both(0, 3, 1).both(3, 4, 1).both(4, 0, 1);
        assertThat("the second loop is left unwalked", new Hierholzer().circuit(graph), hasSize(7));
    }

    private static Graph triangle() {
        return new Graph(6).both(0, 1, 1).both(1, 2, 1).both(2, 0, 1);
    }

    private static int repeats(List<Integer> circuit) {
        Set<String> walked = new HashSet<>();
        int repeats = 0;
        for (int at = 0; at + 1 < circuit.size(); at++) {
            int from = Math.min(circuit.get(at), circuit.get(at + 1));
            int to = Math.max(circuit.get(at), circuit.get(at + 1));
            if (!walked.add(from + "-" + to)) {
                ++repeats;
            }
        }
        return repeats;
    }
}
