package ru.mifi.practice.vol5.graph.algorithms;

import ru.mifi.practice.vol5.graph.Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        private final Graph.Vertex<T, W> target;
        private final Set<String> visited;
        private Graph.Vertex<T, W> current;
        private boolean canContinue;

        private Ant(Random random, Graph<T, W> graph, String source, String target) {
            this.random = random;
            this.graph = graph;
            this.source = graph.getVertex(source);
            this.target = graph.getVertex(target);
            this.visited = new HashSet<>();
            this.current = this.source;
            this.canContinue = true;
        }

        private double random() {
            return random.nextDouble(1.);
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
            String[] vertices = graph.getVertices().toArray(new String[0]);
            int size = vertices.length;
            this.pheromones = new Matrix(size, parameters.kPheromone.doubleValue());
            this.parameters = parameters;
            this.ants = new ArrayList<>(size * 2);
            for (int i = 0; i < size * 2; i++) {
                int v = (int) random.nextDouble(size - 1);
                ants.add(new Ant<>(random, graph, graph.getVertex(vertices[v]).id(), target));
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
