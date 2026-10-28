package ru.mifi.practice.voln.mazes.implementation.finder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.mazes.Maze;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Поиск пути в лабиринте обходом в ширину")
final class NodeFinderTest {

    @DisplayName("Открытый лабиринт даёт кратчайший путь")
    @Test
    @Timeout(1)
    void reachableGoalYieldsShortestPath() {
        assertThat("open maze dont yield the shortest path",
            new NodeFinder().findPath(new Maze.Grid(2, 2)).length, is(3));
    }

    @DisplayName("Путь начинается в левом верхнем углу")
    @Test
    @Timeout(1)
    void pathStartsAtTheEntrance() {
        Maze.Point[] path = new NodeFinder().findPath(new Maze.Grid(2, 2));
        assertThat("path dont start at the entrance", path[0], is(new Maze.Point(0, 0)));
    }

    @DisplayName("Путь заканчивается в правом нижнем углу")
    @Test
    @Timeout(1)
    void pathEndsAtTheExit() {
        Maze.Point[] path = new NodeFinder().findPath(new Maze.Grid(2, 2));
        assertThat("path dont end at the exit", path[path.length - 1], is(new Maze.Point(1, 1)));
    }

    @DisplayName("Замурованный финиш даёт пустой путь")
    @Test
    @Timeout(1)
    void unreachableGoalYieldsEmptyPath() {
        Maze.Grid maze = new Maze.Grid(2, 2);
        maze.set(1, 1, (char) (Maze.SQUARE_LEFT | Maze.SQUARE_UP));
        assertThat("a walled-off exit is still reached",
            new NodeFinder().findPath(maze).length, is(0));
    }
}
