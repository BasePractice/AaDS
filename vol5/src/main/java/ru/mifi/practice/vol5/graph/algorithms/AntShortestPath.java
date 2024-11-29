package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class AntShortestPath<T, W extends Number & Comparable<W>> implements Algorithms.ShortestPath<T, W> {
    @Override
    public List<String> shortestPath(Graph<T, W> graph, String source, String target) {
        Colony<T, W> colony = new Colony<>(new Random(), graph, target, Parameters.started());
        return List.of();
    }

    record Parameters(Number kAlpha,
                      Number kBeta,
                      Number kPheromone,
                      Number kQ,
                      Number kEvaporation,
                      Number kBarrier) {

        static Parameters started() {
            return new Parameters(1., 2., 1, 5., 0.2, 0.01);
        }
    }

    static final class Matrix {
        private final double[][] values;

        Matrix(int row, int col, double initialValue) {
            this.values = new double[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    this.values[i][j] = initialValue;
                }
            }
        }

        Matrix(int quad, double initialValue) {
            this(quad, quad, initialValue);
        }
    }

    private static final class Ant<T, W extends Number & Comparable<W>> {
        private final Random random;
        private final Graph<T, W> graph;
        private final Graph.Vertex<T, W> source;
        private final String target;
        private final Set<Graph.Vertex<T, W>> visited;
        private final List<Graph.Vertex<T, W>> path;
        private double distance;
        private Graph.Vertex<T, W> current;
        private boolean canContinue;

        private Ant(Random random, Graph<T, W> graph, Graph.Vertex<T, W> source, String target) {
            this.random = random;
            this.graph = graph;
            this.source = source;
            this.target = target;
            this.visited = new HashSet<>();
            this.path = new ArrayList<>();
            this.current = source;
            this.canContinue = true;
        }

        private double random() {
            return random.nextDouble(1.);
        }

        void step(Matrix pheromones, Parameters parameters) {
            if (path.isEmpty()) {
                path.add(current);
                visited.add(current);
            }
            List<Graph.Vertex<T, W>> neighbours = new ArrayList<>();
            List<Graph.Edge<T, W>> edges = graph.getEdges(current);
            edges.forEach(edge -> {
                if (!visited.contains(edge.target())) {
                    neighbours.add(edge.target());
                }
            });
            if (neighbours.isEmpty()) {
                canContinue = false;
                edges.stream().filter(e -> e.target().equals(source)).forEach(edge -> {
                    path.add(source);
                    distance += edge.weight().doubleValue();
                });
                return;
            }
            Map<Graph.Vertex<T, W>, Double> choosing = new HashMap<>();
            {
                List<Double> wish = new LinkedList<>();
                double summary = 0;
                for (Graph.Vertex<T, W> v : neighbours) {
                    var tau = pheromones.values[current.index()][v.index()];
                    var weight = edges.stream().filter(e -> e.target().equals(v)).findAny()
                        .map(e -> e.weight().doubleValue()).orElse(0.);
                    var n = 1. / weight;
                    double w = Math.pow(tau, parameters.kAlpha.doubleValue()) * Math.pow(n, parameters.kBeta.doubleValue());
                    wish.add(w);
                    summary += w;
                }

                Graph.Vertex<T, W> prev = null;
                for (int i = 0; i < neighbours.size(); i++) {
                    var vertex = neighbours.get(i);
                    var v = wish.get(i);
                    double p = v / summary;
                    if (i == 0) {
                        choosing.put(vertex, p);
                    } else {
                        choosing.put(vertex, choosing.get(prev) + p);
                    }
                    prev = vertex;
                }
            }
            double v = random();
            Graph.Vertex<T, W> next = null;
            for (Graph.Vertex<T, W> vertex : neighbours) {
                Double d = choosing.get(vertex);
                if (v <= d) {
                    next = vertex;
                    break;
                }
            }
            path.add(next);
            visited.add(next);
            Graph.Vertex<T, W> finalNext = next;
            distance += graph.getEdges(current).stream().filter(e -> e.target().equals(finalNext))
                .findAny().map(e -> e.weight().doubleValue()).orElse(0.);
            current = finalNext;
        }
    }

    private static final class Colony<T, W extends Number & Comparable<W>> {
        private final Random random;
        private final Graph<T, W> graph;
        private final Parameters parameters;
        private final Matrix pheromones;
        private final List<Ant<T, W>> ants;


        private Colony(Random random, Graph<T, W> graph, String target, Parameters parameters) {
            this.random = random;
            this.graph = graph;
            int size = graph.getVertices().size();
            this.pheromones = new Matrix(size, parameters.kPheromone.doubleValue());
            this.parameters = parameters;
            this.ants = new ArrayList<>(size * 2);
            createAnts(random, graph, target);
        }

        private void createAnts(Random random, Graph<T, W> graph, String target) {
            int size = graph.getVertices().size();
            List<Graph.Vertex<T, W>> vertices = graph.getVertices();
            for (int i = 0; i < size * 2; i++) {
                int v = (int) random.nextDouble(size - 1);
                ants.add(new Ant<>(random, graph, vertices.get(v), target));
            }
        }

        private void pheromoneStage(Matrix stage) {
            double barrier = parameters.kBarrier.doubleValue();
            double evaporation = parameters.kEvaporation.doubleValue();
            for (int source = 0; source < stage.values.length; source++) {
                for (int target = 0; target < stage.values.length; target++) {
                    this.pheromones.values[source][target] = (1 - evaporation)
                        * this.pheromones.values[source][target] + stage.values[source][target];
                    if (this.pheromones.values[source][target] < barrier
                        && this.pheromones.values[source][target] != stage.values[source][target]) {
                        this.pheromones.values[source][target] = barrier;
                    }
                }
            }
        }
    }

}
