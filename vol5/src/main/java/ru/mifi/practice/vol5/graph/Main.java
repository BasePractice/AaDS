package ru.mifi.practice.vol5.graph;

import ru.mifi.practice.vol5.graph.loader.StandardLoader;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        Graph<String, Integer> graph = new StandardLoader<String>()
            .load(Objects.requireNonNull(Main.class.getResourceAsStream("/standard.graph")), s -> s);
        var algorithms = new Algorithms.Default<>(graph);
        algorithms.dfs(vertex -> System.out.printf("%s", vertex));
        System.out.println();
        List<String> circle = algorithms.searchCircle();
        System.out.println(circle);
    }
}
