package ru.mifi.practice.vol14.mazes.implementation.finder;

import ru.mifi.practice.vol14.mazes.Maze;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Bidirectional BFS
/** Поиск пути в лабиринте двусторонним обходом в ширину — от старта и финиша одновременно. */
public final class BidirectionalBfsFinder implements Maze.Finder {
    private static final Color PATH_COLOR = Color.BLUE;
    private static final Color BACKWARD_COLOR = Color.MAGENTA;
    private final Maze.Representation repr;
    private final Color pathColor;
    private final Color backwardColor;

    public BidirectionalBfsFinder() {
        this(null);
    }

    public BidirectionalBfsFinder(Maze.Representation repr) {
        this(repr, PATH_COLOR);
    }

    public BidirectionalBfsFinder(Maze.Representation repr, Color pathColor) {
        this(repr, pathColor, BACKWARD_COLOR);
    }

    public BidirectionalBfsFinder(Maze.Representation repr, Color pathColor, Color backwardColor) {
        this.repr = repr;
        this.pathColor = pathColor;
        this.backwardColor = backwardColor;
    }

    @Override
    public Maze.Point[] findPath(Maze.Grid maze) {
        int rows = maze.rows();
        int cols = maze.cols();
        int goalRow = rows - 1;
        int goalCol = cols - 1;
        Cell start = new Cell(0, 0);
        Cell goal = new Cell(goalRow, goalCol);
        Map<Cell, Cell> forwardFrom = new HashMap<>();
        forwardFrom.put(start, null);
        Deque<Cell> forwardQueue = new ArrayDeque<>();
        forwardQueue.add(start);
        Map<Cell, Cell> backwardFrom = new HashMap<>();
        backwardFrom.put(goal, null);
        Deque<Cell> backwardQueue = new ArrayDeque<>();
        backwardQueue.add(goal);
        Frames frames = new Frames();
        Cell meetingPoint = null;
        while (!forwardQueue.isEmpty() && !backwardQueue.isEmpty()) {
            meetingPoint = expand(maze, forwardQueue, forwardFrom, backwardFrom, frames, true);
            if (meetingPoint != null) {
                break;
            }
            meetingPoint = expand(maze, backwardQueue, backwardFrom, forwardFrom, frames, false);
            if (meetingPoint != null) {
                break;
            }
        }
        if (meetingPoint == null) {
            return new Maze.Point[0];
        }
        return buildPath(forwardFrom, backwardFrom, meetingPoint);
    }

    private Cell expand(Maze.Grid maze, Deque<Cell> queue, Map<Cell, Cell> from,
                        Map<Cell, Cell> otherFrom, Frames frames, boolean forward) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            Cell current = queue.poll();
            for (Direction direction : Direction.NEIGHBOURS) {
                int nextRow = current.row() + direction.deltaRow();
                int nextCol = current.col() + direction.deltaCol();
                if (!isOpen(maze, current.row(), current.col(), nextRow, nextCol, direction)) {
                    continue;
                }
                Cell next = new Cell(nextRow, nextCol);
                if (from.containsKey(next)) {
                    continue;
                }
                from.put(next, current);
                frames.record(maze, new Maze.Point(nextCol, nextRow), forward);
                if (otherFrom.containsKey(next)) {
                    return next;
                }
                queue.add(next);
            }
        }
        return null;
    }

    private boolean isOpen(Maze.Grid maze, int row, int col, int nextRow, int nextCol, Direction direction) {
        if (nextRow < 0 || nextRow >= maze.rows() || nextCol < 0 || nextCol >= maze.cols()) {
            return false;
        }
        return (maze.data(row, col) & direction.fromWall()) == 0
            && (maze.data(nextRow, nextCol) & direction.toWall()) == 0;
    }

    private Maze.Point[] buildPath(Map<Cell, Cell> forwardFrom, Map<Cell, Cell> backwardFrom,
                                   Cell meeting) {
        List<Maze.Point> forward = new ArrayList<>();
        for (Cell cell = meeting; cell != null; cell = forwardFrom.get(cell)) {
            forward.add(new Maze.Point(cell.col(), cell.row()));
        }
        List<Maze.Point> backward = new ArrayList<>();
        for (Cell cell = backwardFrom.get(meeting); cell != null; cell = backwardFrom.get(cell)) {
            backward.add(new Maze.Point(cell.col(), cell.row()));
        }
        List<Maze.Point> path = new ArrayList<>();
        for (int i = forward.size() - 1; i >= 0; i--) {
            path.add(forward.get(i));
        }
        for (int i = backward.size() - 1; i >= 0; i--) {
            path.add(backward.get(i));
        }
        return path.toArray(new Maze.Point[0]);
    }

    private record Cell(int row, int col) {
    }

    private record Direction(int deltaRow, int deltaCol, int fromWall, int toWall) {
        private static final Direction[] NEIGHBOURS = {
            new Direction(-1, 0, Maze.SQUARE_UP, Maze.SQUARE_DOWN),
            new Direction(1, 0, Maze.SQUARE_DOWN, Maze.SQUARE_UP),
            new Direction(0, -1, Maze.SQUARE_LEFT, Maze.SQUARE_RIGHT),
            new Direction(0, 1, Maze.SQUARE_RIGHT, Maze.SQUARE_LEFT),
        };
    }

    private final class Frames {
        private final List<Maze.Point> points = new ArrayList<>();
        private int index;

        void record(Maze.Grid maze, Maze.Point point, boolean forward) {
            if (repr == null) {
                return;
            }
            points.add(point);
            repr.snapshot(index, maze, points.toArray(new Maze.Point[0]),
                forward ? pathColor : backwardColor);
            index++;
        }
    }
}
