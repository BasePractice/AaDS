package ru.mifi.practice.vol5.graph.loader;

import ru.mifi.practice.vol5.graph.Graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

@SuppressWarnings({"PMD.EmptyControlStatement", "PMD.CompareObjectsWithEquals"})
public final class StandardWeightLoader<T> implements Graph.Loader<T, Integer> {
    @Override
    public Graph<T, Integer> load(InputStream stream, Function<String, T> value) throws IOException {
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
                weight.append("1");
                current = source;
            } else if (c == '}') {
                String sourceText = source.toString();
                String targetText = target.toString();
                String weightText = weight.toString();
                graph.createVertex(sourceText, value.apply(sourceText));
                graph.createVertex(targetText, value.apply(targetText));
                graph.createEdge(sourceText, targetText, Integer.parseInt(weightText));
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
