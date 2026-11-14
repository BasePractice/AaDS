package ru.mifi.practice.vol5.graph.algorithms;

import org.junit.jupiter.api.Test;
import ru.mifi.practice.vol5.graph.Graph;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrongComponentsTest {
    @Test
    void findsTheLectureComponents() {
        assertEquals(Set.of(Set.of("A", "B", "C"), Set.of("D", "E"), Set.of("F"), Set.of("G")),
            components(StrongComponentsDemo.example()));
    }

    @Test
    void buildsTheLectureCondensationWithoutDuplicateArcs() {
        var result = new StrongComponents().find(StrongComponentsDemo.example());
        var arcs = result.arcs().stream()
            .map(arc -> new LabelArc(result.components().get(arc.source()), result.components().get(arc.target())))
            .collect(Collectors.toSet());
        assertEquals(Set.of(new LabelArc(Set.of("A", "B", "C"), Set.of("D", "E")),
            new LabelArc(Set.of("D", "E"), Set.of("F"))), arcs);
    }

    private record LabelArc(Set<String> source, Set<String> target) {
    }

    @Test
    void returnsAnEmptyPartitionForAnEmptyGraph() {
        assertEquals(Set.of(), components(graph("")));
    }

    @Test
    void discardsASelfLoopInTheCondensation() {
        assertEquals(Set.of(), new StrongComponents().find(graph("A", "AA")).arcs());
    }

    @Test
    void treatsAVertexWithASelfLoopAsOneComponent() {
        assertEquals(Set.of(Set.of("A")), components(graph("A", "AA")));
    }

    @Test
    void groupsAllVerticesOfADirectedCycle() {
        assertEquals(Set.of(Set.of("A", "B", "C")), components(graph("ABC", "AB", "BC", "CA")));
    }

    @Test
    void keepsEachDagVertexInItsOwnComponent() {
        assertEquals(Set.of(Set.of("A"), Set.of("B"), Set.of("C")),
            components(graph("ABC", "AB", "AC", "BC")));
    }

    private Set<Set<String>> components(Graph<String, Integer> graph) {
        return new StrongComponents().find(graph).components().stream().collect(Collectors.toSet());
    }

    private Graph<String, Integer> graph(String labels, String... edges) {
        Graph<String, Integer> graph = new Graph.Standard<>();
        for (String label : labels.split("")) {
            if (!label.isEmpty()) {
                graph.addVertex(label, label);
            }
        }
        for (String edge : edges) {
            graph.getVertex(edge.substring(0, 1)).addEdge(graph.getVertex(edge.substring(1)), 1);
        }
        return graph;
    }
}
