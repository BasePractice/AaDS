package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Лекционный пример разбиения графа и построения конденсации.
 */
public final class StrongComponentsDemo {
    private StrongComponentsDemo() {
    }

    public static void main(String[] args) {
        var result = new StrongComponents().find(example());
        List<String> components = result.components().stream().map(StrongComponentsDemo::format).toList();
        System.out.println("Компоненты: " + String.join(", ", components));
        System.out.println("Дуги конденсации:");
        for (var arc : result.arcs()) {
            System.out.println(components.get(arc.source()) + " → " + components.get(arc.target()));
        }
    }

    static Graph<String, Integer> example() {
        Graph<String, Integer> graph = new Graph.Standard<>();
        for (String label : List.of("A", "B", "C", "D", "E", "F", "G")) {
            graph.addVertex(label, label);
        }
        for (String arc : List.of("AB", "BC", "BD", "CA", "CD", "DE", "ED", "EF")) {
            graph.getVertex(arc.substring(0, 1)).addEdge(graph.getVertex(arc.substring(1)), 1);
        }
        return graph;
    }

    private static String format(Set<String> labels) {
        return labels.stream().sorted().collect(Collectors.joining(", ", "{", "}"));
    }
}
