package ru.mifi.practice.vol5.graph.loader;

import ru.mifi.practice.vol5.graph.Graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

@SuppressWarnings("PMD.EmptyControlStatement")
public final class StandardLoader<T> implements Graph.Loader<T, Integer> {
    @Override
    public Graph<T, Integer> load(InputStream stream, Function<String, T> value) throws IOException {
        int ret;
        StringBuilder source = new StringBuilder();
        StringBuilder target = new StringBuilder();
        StringBuilder current = source;
        Graph<T, Integer> graph = new Graph.Standard<>();
        while ((ret = stream.read()) != -1) {
            char c = (char) ret;
            if (Character.isWhitespace(c)) {
                //Nothing
            } else if (c == '{') {
                source.setLength(0);
                target.setLength(0);
                current = source;
            } else if (c == '}') {
                String sourceText = source.toString();
                String targetText = target.toString();
                graph.createVertex(sourceText, value.apply(sourceText));
                graph.createVertex(targetText, value.apply(targetText));
                graph.createEdge(sourceText, targetText, -1);
            } else if (c == ',') {
                current = target;
            } else {
                current.append(c);
            }
        }
        return graph;
    }
}
