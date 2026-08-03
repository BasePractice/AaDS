package ru.mifi.practice.vol5.graph.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol5.graph.Graph;
import ru.mifi.practice.vol5.graph.loader.ParserText;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Муравьиный алгоритм")
final class AntShortestPathTest {

    @DisplayName("Путь заканчивается в целевой вершине")
    @Test
    @Timeout(30)
    void endsThePathAtTheTarget() {
        assertThat("the algorithm returns a dead end instead of a path to the target",
            last(path()).label(), is("d"));
    }

    @DisplayName("Путь начинается в исходной вершине")
    @Test
    @Timeout(30)
    void startsThePathAtTheSource() {
        assertThat("the path dont start at the source", path().get(0).label(), is("a"));
    }

    @DisplayName("Дешёвый тупик не выдаётся за кратчайший путь")
    @Test
    @Timeout(30)
    void neverReturnsACheapDeadEnd() {
        assertThat("a cheap dead end wins over the real route", path().size(), is(3));
    }

    /**
     * Вершина b — тупик с весом 1, а единственный путь до d стоит 20. Муравей, застрявший
     * в тупике, имеет непустой путь и меньшую длину, поэтому без проверки прибытия в цель
     * именно он и объявляется «кратчайшим путём».
     */
    private static List<Graph.Vertex<String, Integer>> path() {
        Graph<String, Integer> graph = graph("{a,b,1}{a,c,10}{c,d,10}");
        return new AntShortestPath<String, Integer>()
            .shortestPath(graph, graph.getVertex("a"), graph.getVertex("d"));
    }

    private static Graph.Vertex<String, Integer> last(List<Graph.Vertex<String, Integer>> path) {
        return path.get(path.size() - 1);
    }

    private static Graph<String, Integer> graph(String text) {
        try {
            return new ParserText<String>().parse(text, value -> value);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось разобрать граф", e);
        }
    }
}
