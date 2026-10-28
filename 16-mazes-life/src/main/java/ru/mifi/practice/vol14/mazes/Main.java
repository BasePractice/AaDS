package ru.mifi.practice.vol14.mazes;

import ru.mifi.practice.vol14.images.Gif;
import ru.mifi.practice.vol14.mazes.implementation.GifRepresentation;
import ru.mifi.practice.vol14.mazes.implementation.finder.AStarFinder;
import ru.mifi.practice.vol14.mazes.implementation.finder.BidirectionalBfsFinder;
import ru.mifi.practice.vol14.mazes.implementation.finder.DfsFinder;
import ru.mifi.practice.vol14.mazes.implementation.finder.NodeFinder;
import ru.mifi.practice.vol14.mazes.implementation.generator.KruskalGenerator;
import ru.mifi.practice.vol14.mazes.implementation.generator.NodeGenerator;
import ru.mifi.practice.vol14.mazes.implementation.generator.PrimGenerator;
import ru.mifi.practice.vol14.mazes.implementation.generator.RecursiveDivisionGenerator;

import java.awt.Color;
import java.io.IOException;

/** Демонстрация всех комбинаций генераторов и поисковиков лабиринта с анимацией GIF. */
public abstract class Main {
    private static final int ROWS = 20;
    private static final int COLS = 20;
    private static final int DELAY_MS = 50;

    public static void main(String[] args) throws IOException {
        for (Maze.Generator generator : generators()) {
            for (FinderConfig config : finders()) {
                GifRepresentation representation = new GifRepresentation();
                Maze.Finder finder = config.create(representation);
                Maze.Grid grid = generator.generate(ROWS, COLS);
                Maze.Point[] path = finder.findPath(grid);
                representation.representation(name(generator, config), grid, path);
                Gif.create2(representation, String.format("%s-%s.gif", genName(generator), config.name()),
                    DELAY_MS, true);
            }
        }
    }

    private static String name(Maze.Generator generator, FinderConfig config) {
        return String.format("%s-%s", genName(generator), config.name());
    }

    private static String genName(Maze.Generator generator) {
        if (generator instanceof NodeGenerator) {
            return "dfs";
        }
        if (generator instanceof PrimGenerator) {
            return "prim";
        }
        if (generator instanceof KruskalGenerator) {
            return "kruskal";
        }
        if (generator instanceof RecursiveDivisionGenerator) {
            return "division";
        }
        return "unknown";
    }

    private static Maze.Generator[] generators() {
        return new Maze.Generator[]{
            new NodeGenerator(),
            new PrimGenerator(),
            new KruskalGenerator(),
            new RecursiveDivisionGenerator(),
        };
    }

    private static FinderConfig[] finders() {
        return new FinderConfig[]{
            new FinderConfig("bfs", Color.BLUE, FinderConfig.FinderFactory.of(NodeFinder::new)),
            new FinderConfig("astar", Color.GREEN, FinderConfig.FinderFactory.of(AStarFinder::new)),
            new FinderConfig("dfs", Color.ORANGE, FinderConfig.FinderFactory.of(DfsFinder::new)),
            new FinderConfig("bidir", Color.MAGENTA, FinderConfig.FinderFactory.of(BidirectionalBfsFinder::new)),
        };
    }

    private record FinderConfig(String name, Color color, FinderFactory factory) {
        Maze.Finder create(Maze.Representation repr) {
            return factory.create(repr);
        }
    }

    private interface FinderFactory {
        Maze.Finder create(Maze.Representation repr);

        static FinderFactory of(FinderFactory factory) {
            return factory;
        }
    }
}
