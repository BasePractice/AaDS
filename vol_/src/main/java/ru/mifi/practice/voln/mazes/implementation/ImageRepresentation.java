package ru.mifi.practice.voln.mazes.implementation;

import ru.mifi.practice.voln.mazes.Maze;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.Color.BLUE;
import static java.awt.Color.DARK_GRAY;

public record ImageRepresentation(int width,
                                  int thickness,
                                  Color lineColor,
                                  Color pathColor) implements Maze.Representation {

    public ImageRepresentation() {
        this(40, 4, DARK_GRAY, BLUE);
    }

    static void drawPath(Graphics2D g, Maze.Point[] path, Color color, int width, int thickness) {
        final Stroke lastStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness));
        final Color lastColor = g.getColor();
        g.setColor(color);
        int lastX = -1;
        int lastY = -1;
        for (Maze.Point p : path) {
            int halfWidth = width / 2;
            int xCenter = (p.x() * width) + halfWidth;
            int yCenter = (p.y() * width) + halfWidth;
            if (lastX >= 0 && lastY >= 0) {
                g.drawLine(lastX, lastY, xCenter, yCenter);
            }
            lastX = xCenter;
            lastY = yCenter;
        }
        g.setStroke(lastStroke);
        g.setColor(lastColor);
    }

    static void drawMaze(Graphics2D g, Maze.Grid maze, Color color, int width, int thickness) {
        final Stroke lastStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness));
        final Color lastColor = g.getColor();
        g.setColor(color);
        for (int row = 0; row < maze.rows(); ++row) {
            for (int col = 0; col < maze.cols(); ++col) {
                int halfWidth = width / 2;
                int xCenter = (col * width) + halfWidth;
                int yCenter = (row * width) + halfWidth;

                g.setColor(Color.RED);
                drawCenteredCircle(g, xCenter, yCenter, thickness);
                g.setColor(color);
                if ((maze.data(row, col) & Maze.SQUARE_LEFT) == Maze.SQUARE_LEFT) {
                    g.drawLine(xCenter - halfWidth, yCenter + halfWidth, xCenter - halfWidth, yCenter - halfWidth);
                }
                if ((maze.data(row, col) & Maze.SQUARE_UP) == Maze.SQUARE_UP) {
                    g.drawLine(xCenter - halfWidth, yCenter - halfWidth, xCenter + halfWidth, yCenter - halfWidth);
                }
                if ((maze.data(row, col) & Maze.SQUARE_RIGHT) == Maze.SQUARE_RIGHT) {
                    g.drawLine(xCenter + halfWidth, yCenter - halfWidth, xCenter + halfWidth, yCenter + halfWidth);
                }
                if ((maze.data(row, col) & Maze.SQUARE_DOWN) == Maze.SQUARE_DOWN) {
                    g.drawLine(xCenter - halfWidth, yCenter + halfWidth, xCenter + halfWidth, yCenter + halfWidth);
                }
            }
        }
        g.setStroke(lastStroke);
        g.setColor(lastColor);
    }

    private static void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
        x = x - (r / 2);
        y = y - (r / 2);
        g.fillOval(x, y, r, r);
    }

    private BufferedImage createImage(Maze.Grid maze, Maze.Point[] path) {
        BufferedImage result = new BufferedImage(maze.cols() * width + width, maze.rows() * width + width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawMaze(g, maze, lineColor, width, thickness);
        if (path != null) {
            drawPath(g, path, pathColor, width, thickness);
        }
        return result;
    }

    private BufferedImage createSnapshot(Maze.Grid maze, Maze.Point[] points, Color color) {
        BufferedImage result = new BufferedImage(maze.cols() * width + width, maze.rows() * width + width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawMaze(g, maze, lineColor, width, thickness);
        final Stroke lastStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness));
        final Color lastColor = g.getColor();
        g.setColor(color);
        for (Maze.Point p : points) {
            int halfWidth = width / 2;
            int xCenter = (p.x() * width) + halfWidth;
            int yCenter = (p.y() * width) + halfWidth;
            drawCenteredCircle(g, xCenter, yCenter, thickness);
        }
        g.setStroke(lastStroke);
        g.setColor(lastColor);
        return result;
    }

    @Override
    public void representation(String name, Maze.Grid grid, Maze.Point[] path) {
        BufferedImage image = createImage(grid, path);
        File output = new File(String.format("%03dx%03d-%s.png", grid.rows(), grid.cols(), name));
        try {
            ImageIO.write(image, "PNG", output);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write representation image to file: " + output, ex);
        }
    }

    @Override
    public void snapshot(int index, Maze.Grid grid, Maze.Point[] points, Color color) {
        BufferedImage image = createSnapshot(grid, points, color);
        File output = new File(String.format("%03dx%03d-snapshot-%04d.png", grid.rows(), grid.cols(), index));
        try {
            ImageIO.write(image, "PNG", output);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write snapshot image to file: " + output, ex);
        }
    }
}
