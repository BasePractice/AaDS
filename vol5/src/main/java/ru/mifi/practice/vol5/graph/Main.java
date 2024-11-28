package ru.mifi.practice.vol5.graph;

import java.util.List;

public abstract class Main {
    public static void main(String[] args) {
        Graph<Void, Integer> graph = Factory.createGraph();
        graph.createVertex(null);
        graph.createVertex(null);
        graph.createVertex(null);
        graph.createVertex(null);
        graph.createVertex(null);
        graph.createEdge("1", "2", 0);
        graph.createEdge("1", "3", 0);
        graph.createEdge("2", "5", 0);
        graph.createEdge("3", "4", 0);
        graph.createEdge("4", "5", 0);
        graph.createEdge("5", "3", 0);
        var algorithms = new Algorithms.Default<>(graph);
        algorithms.dfs(vertex -> System.out.printf("%s", vertex));
        System.out.println();
        List<String> circle = algorithms.searchCircle();
        System.out.println(circle);
    }
}
