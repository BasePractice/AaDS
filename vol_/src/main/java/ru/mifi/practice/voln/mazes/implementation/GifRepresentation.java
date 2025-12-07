package ru.mifi.practice.voln.mazes.implementation;

import lombok.NonNull;
import ru.mifi.practice.voln.mazes.Maze;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.DARK_GRAY;

public final class GifRepresentation implements Maze.Representation, Iterable<BufferedImage> {
    private final List<BufferedImage> images = new ArrayList<>();
    private final ImageRepresentation image = new ImageRepresentation(40, 4, 8, DARK_GRAY, BLUE);

    @Override
    public void representation(String name, Maze.Grid grid, Maze.Point[] path) {
        image.representation(name, grid, path);
    }

    @Override
    public void snapshot(int index, Maze.Grid grid, Maze.Point[] points, Color color) {
        BufferedImage snapshot = image.createSnapshot(index, grid, points, color);
        images.add(snapshot);
    }

    @NonNull
    @Override
    public Iterator<BufferedImage> iterator() {
        return new ArrayList<>(images).iterator();
    }
}
