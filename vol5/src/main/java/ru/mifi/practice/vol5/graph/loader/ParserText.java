package ru.mifi.practice.vol5.graph.loader;

import ru.mifi.practice.vol5.graph.Graph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@SuppressWarnings({"PMD.EmptyControlStatement", "PMD.CompareObjectsWithEquals"})
public final class ParserText<T> implements Graph.Loader<String, T, Integer> {

    public Graph<T, Integer> parse(String text, Function<String, T> value) throws IOException {
        return parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), value, Integer::parseInt);
    }

    @Override
    public Graph<T, Integer> parse(InputStream stream, Function<String, T> value, Function<String, Integer> weightFun) throws IOException {
        int ret;
        StringBuilder source = new StringBuilder();
        StringBuilder target = new StringBuilder();
        StringBuilder weight = new StringBuilder();
        StringBuilder current = source;
        Graph<T, Integer> graph = new Graph.Standard<>();
        while ((ret = stream.read()) != -1) {
            char c = (char) ret;
            if (Character.isWhitespace(c)) {
                //Nothing
            } else if (c == '{') {
                source.setLength(0);
                target.setLength(0);
                weight.setLength(0);
                current = source;
            } else if (c == '}') {
                String sourceText = source.toString();
                String targetText = target.toString();
                String weightText = weight.toString();
                if (weightText.isEmpty()) {
                    weightText = "1";
                }
                Graph.Vertex<T, Integer> sourceVertex = graph.addVertex(sourceText, value.apply(sourceText));
                Graph.Vertex<T, Integer> targetVertex = graph.addVertex(targetText, value.apply(targetText));
                sourceVertex.addEdge(targetVertex, weightFun.apply(weightText));
            } else if (c == ',') {
                if (current == source) {
                    current = target;
                } else if (current == target) {
                    weight.setLength(0);
                    current = weight;
                }
            } else {
                current.append(c);
            }
        }
        return graph;
    }
}
