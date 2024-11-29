package ru.mifi.practice.vol5.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public interface Graph<T, W extends Number & Comparable<W>> {

    int size();

    int indexOf(String label);

    Vertex<T, W> ofIndex(int index);

    Vertex<T, W> createVertex(T value);

    Vertex<T, W> createVertex(String label, T value);

    Edge<T, W> createEdge(Vertex<T, W> source, Vertex<T, W> target, W weight);

    Edge<T, W> createEdge(String source, String target, W weight);

    List<Edge<T, W>> getEdges(String source);

    default List<Edge<T, W>> getEdges(Vertex<T, W> source) {
        return getEdges(source.label());
    }

    Vertex<T, W> getVertex(String source);

    List<Graph.Vertex<T, W>> getVertices();

    @FunctionalInterface
    interface Loader<T, W extends Number & Comparable<W>> {
        Graph<T, W> load(InputStream stream, Function<String, T> value) throws IOException;
    }

    interface Vertex<T, W extends Number & Comparable<W>> {

        String label();

        int index();

        T value();

        final class Default<T, W extends Number & Comparable<W>> implements Vertex<T, W> {
            private final int index;
            private final String label;
            private final T value;

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

        final class Default<T, W extends Number & Comparable<W>> implements Edge<T, W> {
            private final Vertex<T, W> source;
            private final Vertex<T, W> target;
            private final W weight;

            Default(Vertex<T, W> source, Vertex<T, W> target, W weight) {
                this.source = source;
                this.target = target;
                this.weight = weight;
            }

            @Override
            public Vertex<T, W> source() {
                return source;
            }

            @Override
            public Vertex<T, W> target() {
                return target;
            }

            @Override
            public W weight() {
                return weight;
            }

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
        protected final List<List<Edge<T, W>>> edges = new ArrayList<>();
        protected final Map<String, Integer> labelIndexes = new HashMap<>();


        @Override
        public int size() {
            return vertices.size();
        }

        @Override
        public Vertex<T, W> ofIndex(int index) {
            Objects.checkIndex(index, vertices.size());
            return vertices.get(index);
        }

        @Override
        public int indexOf(String label) {
            Integer index = labelIndexes.get(label);
            Objects.requireNonNull(index, "Index not found");
            return index;
        }

        @Override
        public Vertex<T, W> getVertex(String label) {
            return vertices.get(indexOf(label));
        }

        @Override
        public List<Edge<T, W>> getEdges(String label) {
            List<Edge<T, W>> list = edges.get(indexOf(label));
            return list == null ? List.of() : list;
        }

        @Override
        public Vertex<T, W> createVertex(T value) {
            return createVertex(null, value);
        }

        @Override
        public Vertex<T, W> createVertex(String label, T value) {
            final int index;
            if (labelIndexes.containsKey(label)) {
                index = labelIndexes.get(label);
            } else {
                index = nextVertexIndex();
            }
            if (label == null) {
                label = String.valueOf(index);
            }
            Vertex.Default<T, W> vertex = new Vertex.Default<>(index, label, value);
            labelIndexes.put(label, index);
            if (index < vertices.size()) {
                vertices.set(index, vertex);
            } else {
                vertices.add(vertex);
                edges.add(new ArrayList<>());
            }
            Objects.checkIndex(index, vertices.size());
            Objects.checkIndex(index, edges.size());
            return vertex;
        }

        @Override
        public Edge<T, W> createEdge(Vertex<T, W> source, Vertex<T, W> target, W weight) {
            Edge.Default<T, W> edge = new Edge.Default<>(source, target, weight);
            List<Edge<T, W>> list = edges.get(source.index());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(edge);
            edges.set(source.index(), list);
            return edge;
        }

        @Override
        public Edge<T, W> createEdge(String source, String target, W weight) {
            Vertex<T, W> sourceVertex = getVertex(source);
            Vertex<T, W> targetVertex = getVertex(target);
            return createEdge(sourceVertex, targetVertex, weight);
        }

        protected abstract int nextVertexIndex();

        @Override
        public List<Graph.Vertex<T, W>> getVertices() {
            return vertices;
        }
    }

    final class Standard<T, W extends Number & Comparable<W>> extends AbstractGraph<T, W> {
        private final AtomicInteger vertexCount = new AtomicInteger(0);

        @Override
        protected int nextVertexIndex() {
            return vertexCount.getAndIncrement();
        }
    }
}
