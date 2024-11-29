package ru.mifi.practice.vol5.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public interface Graph<T, W extends Number & Comparable<W>> {

    Vertex<T, W> createVertex(T value);

    Vertex<T, W> createVertex(String id, T value);

    Edge<T, W> createEdge(Vertex<T, W> source, Vertex<T, W> target, W weight);

    Edge<T, W> createEdge(String source, String target, W weight);

    List<Edge<T, W>> getEdges(String source);

    Vertex<T, W> getVertex(String source);

    Set<String> getVertices();


    @FunctionalInterface
    interface Loader<T, W extends Number & Comparable<W>> {
        Graph<T, W> load(InputStream stream, Function<String, T> value) throws IOException;
    }

    interface Vertex<T, W extends Number & Comparable<W>> {

        String id();

        T value();

        final class Default<T, W extends Number & Comparable<W>> implements Vertex<T, W> {
            private final String id;
            private final T value;

            Default(String id, T value) {
                this.id = id;
                this.value = value;
            }

            @Override
            public String id() {
                return id;
            }

            @Override
            public T value() {
                return value;
            }

            @Override
            public String toString() {
                return "(" + id + ")";
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
                return source().id() + ":" + target().id() + ":" + weight();
            }

            @Override
            public String toString() {
                return "{" + id() + "}";
            }
        }
    }

    abstract class AbstractGraph<T, W extends Number & Comparable<W>> implements Graph<T, W> {
        protected final Map<String, Vertex<T, W>> vertices = new HashMap<>();
        protected final Map<String, List<Edge<T, W>>> edges = new HashMap<>();

        @Override
        public Vertex<T, W> getVertex(String id) {
            return vertices.get(id);
        }

        @Override
        public List<Edge<T, W>> getEdges(String id) {
            return edges.getOrDefault(id, List.of());
        }

        @Override
        public Vertex<T, W> createVertex(T value) {
            return createVertex(nextVertexId(), value);
        }

        @Override
        public Vertex<T, W> createVertex(String id, T value) {
            return vertices.computeIfAbsent(id, s -> new Vertex.Default<>(id, value));
        }

        @Override
        public Edge<T, W> createEdge(Vertex<T, W> source, Vertex<T, W> target, W weight) {
            Edge.Default<T, W> edge = new Edge.Default<>(source, target, weight);
            edges.computeIfAbsent(source.id(), k -> new ArrayList<>()).add(edge);
            return edge;
        }

        @Override
        public Edge<T, W> createEdge(String source, String target, W weight) {
            Vertex<T, W> sourceVertex = vertices.get(source);
            Objects.requireNonNull(sourceVertex, "Source vertex not found");
            Vertex<T, W> targetVertex = vertices.get(target);
            Objects.requireNonNull(targetVertex, "Target vertex not found");
            return createEdge(sourceVertex, targetVertex, weight);
        }

        protected abstract String nextVertexId();

        @Override
        public Set<String> getVertices() {
            return vertices.keySet();
        }
    }

    final class Standard<T, W extends Number & Comparable<W>> extends AbstractGraph<T, W> {
        private final AtomicInteger vertexCount = new AtomicInteger(0);

        @Override
        protected String nextVertexId() {
            return String.valueOf(vertexCount.incrementAndGet());
        }
    }
}
