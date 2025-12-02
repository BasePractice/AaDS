package ru.mifi.practice.voln.maze;

import ru.mifi.practice.voln.maze.implementation.ImageRepresentation;
import ru.mifi.practice.voln.maze.implementation.finder.NodeFinder;
import ru.mifi.practice.voln.maze.implementation.generator.NodeGenerator;

public abstract class Main {
    public static void main(String[] args) {
        Maze.Generator generator = new NodeGenerator();
        Maze.Finder finder = new NodeFinder();
        Maze.Representation representation = new ImageRepresentation();
        for (int i = 0; i < 9; i++) {
            generate(generator, finder, representation, 10, 10, "N" + i);
        }
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
