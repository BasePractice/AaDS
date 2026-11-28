package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.Weight;

/** Сравнение попыток релаксации при поиске расстояний между всеми парами. */
public final class ShortestPathsComparison {
    private ShortestPathsComparison() {
    }

    public static void main(String[] args) {
        Graph<String, Integer> graph = new Graph.Standard<>();
        var first = graph.addVertex("A", "A");
        var second = graph.addVertex("B", "B");
        var third = graph.addVertex("C", "C");
        first.addEdge(second, 1);
        first.addEdge(third, 10);
        second.addEdge(third, 2);
        var dijkstra = new DijkstraShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger());
        var ford = new BellmanFordShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger());
        var floyd = new FloydWarshallShortestPath<String, Integer>(Integer.MAX_VALUE, 0, Weight.ofInteger());
        Counter dijkstraCount = Counter.create();
        Counter fordCount = Counter.create();
        Counter floydCount = Counter.create();
        var table = floyd.distances(graph, floydCount);
        for (var source : graph.getVertices()) {
            var expected = table.get(source);
            if (!expected.equals(dijkstra.distances(graph, source, dijkstraCount))
                || !expected.equals(ford.distances(graph, source, fordCount))) {
                throw new IllegalStateException("Shortest path algorithms disagree");
            }
        }
        System.out.println("Все пары: A→B=1, A→C=3, B→C=2; обратные пути недостижимы.");
        System.out.println("Дейкстра: " + dijkstraCount.count());
        System.out.println("Беллман — Форд: " + fordCount.count());
        System.out.println("Флойд — Уоршелл: " + floydCount.count());
    }
}
