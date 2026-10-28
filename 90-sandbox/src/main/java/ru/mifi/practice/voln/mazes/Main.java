package ru.mifi.practice.voln.mazes;

import ru.mifi.practice.voln.images.Gif;
import ru.mifi.practice.voln.mazes.implementation.GifRepresentation;
import ru.mifi.practice.voln.mazes.implementation.finder.NodeFinder;
import ru.mifi.practice.voln.mazes.implementation.generator.NodeGenerator;

import java.io.IOException;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        Maze.Generator generator = new NodeGenerator();
        GifRepresentation representation = new GifRepresentation();
        Maze.Finder finder = new NodeFinder(representation);
        for (int i = 0; i < 1; i++) {
            generate(generator, finder, representation, 20, 20, "N" + i);
        }
        Gif.create2(representation, "maze.gif", 100, true);
    }

    private static void generate(Maze.Generator generator,
                                 Maze.Finder finder,
                                 Maze.Representation representation,
                                 int rows, int cols,
                                 String name) {
        Maze.Grid grid = generator.generate(rows, cols);
        Maze.Point[] path = finder.findPath(grid);
        representation.representation(name, grid, path);
    }

}
