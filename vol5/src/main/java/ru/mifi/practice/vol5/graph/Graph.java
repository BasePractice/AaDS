package ru.mifi.practice.vol5.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public interface Graph<T, W extends Number & Comparable<W>> {

    int size();

    default boolean notEmpty() {
        return size() > 0;
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    Vertex<T, W> addVertex(String label, T value);

    Vertex<T, W> getVertex(String source);

    Vertex<T, W> getVertex(int index);

    List<Graph.Vertex<T, W>> getVertices();

    @FunctionalInterface
    interface Loader<I, T, W extends Number & Comparable<W>> {
        Graph<T, W> parse(InputStream stream, Function<I, T> value, Function<I, W> weight) throws IOException;
    }

    interface Vertex<T, W extends Number & Comparable<W>> {

        String label();

        int index();

        T value();

        List<Graph.Edge<T, W>> edges();

        Edge<T, W> addEdge(Vertex<T, W> target, W weight);

        final class Default<T, W extends Number & Comparable<W>> implements Vertex<T, W> {
            private final int index;
            private final String label;
            private final T value;
            private final List<Graph.Edge<T, W>> edges = new ArrayList<>();

            Default(int index, String label, T value) {
                this.index = index;
                this.label = label;
                this.value = value;
            }

            @Override
            public String label() {
                return label;
            }

            @Override
            public int index() {
                return index;
            }

            @Override
            public T value() {
                return value;
            }

            @Override
            public List<Edge<T, W>> edges() {
                return edges;
            }

            @Override
            public Edge<T, W> addEdge(Vertex<T, W> target, W weight) {
                Edge<T, W> edge = new Edge.Default<>(this, target, weight);
                edges.add(edge);
                return edge;
            }

            @Override
            public String toString() {
                return "(" + label + ")";
            }

            @Override
            public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Default<?, ?> aDefault = (Default<?, ?>) o;
                return index == aDefault.index;
            }

            @Override
            public int hashCode() {
                return Objects.hash(index);
            }
        }
    }

    interface Edge<T, W extends Number & Comparable<W>> {
        Vertex<T, W> source();

        Vertex<T, W> target();

        W weight();

        String id();

        record Default<T, W extends Number & Comparable<W>>(Vertex<T, W> source, Vertex<T, W> target,
                                                            W weight) implements Edge<T, W> {

            @Override
            public String id() {
                return source().label() + ":" + target().label() + ":" + weight();
            }

            @Override
            public String toString() {
                return "{" + id() + "}";
            }
        }
    }

    abstract class AbstractGraph<T, W extends Number & Comparable<W>> implements Graph<T, W> {
        protected final List<Vertex<T, W>> vertices = new ArrayList<>();
        protected final Map<String, Integer> labelIndexes = new HashMap<>();


        @Override
        public int size() {
            return vertices.size();
        }

        public int indexOf(String label) {
            Integer index = labelIndexes.get(label);
            Objects.requireNonNull(index, "Index not found");
            return index;
        }

        @Override
        public Vertex<T, W> getVertex(String label) {
            return getVertex(indexOf(label));
        }

        @Override
        public Vertex<T, W> getVertex(int index) {
            Objects.checkIndex(index, size());
            return vertices.get(index);
        }

        @Override
        public Vertex<T, W> addVertex(String label, T value) {
            final Vertex<T, W> vertex;
            if (!hasVertex(label)) {
                vertex = new Vertex.Default<>(size(), label, value);
                vertices.add(vertex);
                labelIndexes.put(label, vertex.index());
            } else {
                vertex = getVertex(label);
            }
            return vertex;
        }

        @Override
        public List<Graph.Vertex<T, W>> getVertices() {
            return vertices;
        }

        private boolean hasVertex(String label) {
            return labelIndexes.containsKey(label);
        }
    }

    final class Standard<T, W extends Number & Comparable<W>> extends AbstractGraph<T, W> {
    }
}
