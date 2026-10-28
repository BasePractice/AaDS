package ru.mifi.practice.vol12;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

/**
 * Задание слайда 584: евклидово остовное дерево. Реализуйте EuclideanTree.Plane, снимите
 * {@code @Disabled} и добейтесь зелёных тестов.
 */
@DisplayName("Евклидово остовное дерево")
final class EuclideanTreeTest {

    @Disabled("Учебное задание слайда 584: евклидово MST")
    @DisplayName("Соединяет углы единичного квадрата тремя сторонами")
    @Test
    @Timeout(1)
    void joinsTheCornersOfAUnitSquareWithThreeSides() {
        List<EuclideanTree.Point> square = List.of(
            new EuclideanTree.Point(0, 0), new EuclideanTree.Point(1, 0),
            new EuclideanTree.Point(1, 1), new EuclideanTree.Point(0, 1)
        );
        assertThat("the square tree takes a diagonal", new EuclideanTree.Plane().length(square), closeTo(3.0, 1e-9));
    }

    @Disabled("Учебное задание слайда 584: точки на прямой")
    @DisplayName("Точки на прямой соединяются соседями")
    @Test
    @Timeout(1)
    void joinsPointsOnALineByNeighbours() {
        double offset = new Random().nextInt(100);
        List<EuclideanTree.Point> line = List.of(
            new EuclideanTree.Point(offset + 6, 0), new EuclideanTree.Point(offset, 0),
            new EuclideanTree.Point(offset + 3, 0), new EuclideanTree.Point(offset + 1, 0)
        );
        assertThat("points on a line dont join by neighbours", new EuclideanTree.Plane().length(line), closeTo(6.0, 1e-9));
    }

    @Disabled("Учебное задание слайда 584: тысяча точек")
    @DisplayName("Тысяча точек укладывается в секунду")
    @Test
    @Timeout(1)
    void buildsTheTreeOfAThousandPointsWithinASecond() {
        Random random = new Random();
        List<EuclideanTree.Point> points = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            points.add(new EuclideanTree.Point(random.nextDouble(), random.nextDouble()));
        }
        assertThat("a thousand points give a non positive length", new EuclideanTree.Plane().length(points) > 0, is(true));
    }
}
