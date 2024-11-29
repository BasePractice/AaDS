package ru.mifi.practice.vol5.graph;

import ru.mifi.practice.vol5.graph.loader.StandardLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        Graph<String, Integer> graph = new StandardLoader<String>()
            .load(Objects.requireNonNull(Main.class.getResourceAsStream("/standard.graph")), s -> s);
        var algorithms = new Algorithms.Default<String, Integer>();
        System.out.print("DFS: ");
        algorithms.dfs(graph, vertex -> System.out.printf("%s", vertex));
        System.out.println();
        System.out.print("BFS: ");
        algorithms.bfs(graph, vertex -> System.out.printf("%s", vertex));
        System.out.println();
        List<String> circle = algorithms.searchCircle(graph);
        System.out.println("CRL: " + circle);
        var dist = new Algorithms.DijkstraShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Integer::sum);
        Map<String, Integer> distances = dist.distances(graph, "1");
        System.out.println("DST: " + distances);
    }
}