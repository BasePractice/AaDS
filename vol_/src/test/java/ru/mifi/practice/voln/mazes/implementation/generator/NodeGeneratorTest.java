package ru.mifi.practice.voln.mazes.implementation.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.mazes.Maze;
import ru.mifi.practice.voln.mazes.implementation.finder.NodeFinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

/**
 * Проверка генерации лабиринта обходом в глубину.
 *
 * <p>Генератор случаен, поэтому сравнивать его вывод не с чем. Зато у совершенного лабиринта есть
 * свойство, которое обязано выполняться на любом прогоне: из входа достижим выход. Оно и
 * проверяется — поиском пути, то есть другим алгоритмом, а не повторением того же кода.
 */
@DisplayName("Генерация лабиринта обходом в глубину")
final class NodeGeneratorTest {

    @DisplayName("Лабиринт получается заказанного размера")
    @Test
    @Timeout(10)
    void buildsTheRequestedSize() {
        assertThat("the maze dont get the requested size",
            new NodeGenerator().generate(7, 5).rows(), is(7));
    }

    @DisplayName("Лабиринт получается заказанной ширины")
    @Test
    @Timeout(10)
    void buildsTheRequestedWidth() {
        assertThat("the maze dont get the requested width",
            new NodeGenerator().generate(7, 5).cols(), is(5));
    }

    @DisplayName("Из входа всегда есть путь к выходу")
    @Test
    @Timeout(30)
    void alwaysConnectsTheEntranceToTheExit() {
        int unreachable = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            if (new NodeFinder().findPath(new NodeGenerator().generate(8, 8)).length == 0) {
                ++unreachable;
            }
        }
        assertThat("a generated maze leaves the exit unreachable", unreachable, is(0));
    }

    @DisplayName("Лабиринт из одной клетки строится")
    @Test
    @Timeout(10)
    void buildsASingleCellMaze() {
        assertThat("a single cell maze dont get built",
            new NodeGenerator().generate(1, 1).rows(), is(1));
    }

    /** Путь в совершенном лабиринте единственный, но не кратчайший на сетке: он петляет. */
    @DisplayName("Вытянутый лабиринт связен из конца в конец")
    @Test
    @Timeout(10)
    void connectsAStretchedMaze() {
        assertThat("a stretched maze is not connected end to end",
            new NodeFinder().findPath(new NodeGenerator().generate(2, 20)).length,
            is(greaterThanOrEqualTo(21)));
    }
}
