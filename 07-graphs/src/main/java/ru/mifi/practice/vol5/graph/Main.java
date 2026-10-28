package ru.mifi.practice.vol5.graph;

import ru.mifi.practice.vol5.graph.algorithms.AntShortestPath;
import ru.mifi.practice.vol5.graph.algorithms.Base;
import ru.mifi.practice.vol5.graph.algorithms.DijkstraShortestPath;
import ru.mifi.practice.vol5.graph.loader.ParserText;

import java.io.IOException;
import java.util.Objects;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        Graph<String, Integer> graph = new ParserText<String>()
            .parse(Objects.requireNonNull(Main.class.getResourceAsStream("/standard.graph")), s -> s, Integer::parseInt);
        var algorithms = new Base<String, Integer>();
        System.out.print("DFS: ");
        algorithms.dfs(graph, vertex -> System.out.printf("%s", vertex));
        System.out.println();
        System.out.print("BFS: ");
        algorithms.bfs(graph, vertex -> System.out.printf("%s", vertex));
        System.out.println();
        var circle = algorithms.searchCircle(graph);
        System.out.println("CRL: " + circle);
        var dist = new DijkstraShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger());
        var distances = dist.distances(graph, graph.getVertex("1"));
        System.out.println("DST: " + distances);
        graph = new ParserText<String>()
            .parse(Objects.requireNonNull(Main.class.getResourceAsStream("/standard-weight.graph")), s -> s, Integer::parseInt);
        dist = new DijkstraShortestPath<>(Integer.MAX_VALUE, 0, Weight.ofInteger());
        distances = dist.distances(graph, graph.getVertex("1"));
        System.out.println("DST: " + distances);
        var path = new AntShortestPath<String, Integer>();
        var shortest = path.shortestPath(graph, graph.getVertex("1"), graph.getVertex("3"));
        System.out.println("ANT: " + shortest);
    }
}
