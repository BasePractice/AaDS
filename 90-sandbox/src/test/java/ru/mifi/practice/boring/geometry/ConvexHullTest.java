package ru.mifi.practice.boring.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@DisplayName("Выпуклая оболочка монотонной цепочкой")
final class ConvexHullTest {

    @DisplayName("Оболочка квадрата — его углы")
    @Test
    @Timeout(1)
    void takesTheCornersOfTheSquare() {
        assertThat("the hull of the square is not its four corners",
            new ConvexHull().hull(square()).size(), is(4));
    }

    @DisplayName("Внутренняя точка в оболочку не попадает")
    @Test
    @Timeout(1)
    void leavesTheInnerPointOut() {
        assertThat("the inner point got into the hull",
            new ConvexHull().hull(square()).vertices(), not(hasItem(new Point(2, 2))));
    }

    @DisplayName("Точка на стороне в оболочку не попадает")
    @Test
    @Timeout(1)
    void leavesThePointOnTheEdgeOut() {
        List<Point> points = List.of(
            new Point(0, 0), new Point(4, 0), new Point(4, 4), new Point(0, 4), new Point(2, 0));
        assertThat("the point on the edge got into the hull",
            new ConvexHull().hull(points).vertices(), not(hasItem(new Point(2, 0))));
    }

    @DisplayName("Оболочка обходится против часовой стрелки")
    @Test
    @Timeout(1)
    void walksCounterclockwise() {
        assertThat("the hull is walked clockwise",
            new ConvexHull().hull(square()).counterclockwise(), is(true));
    }

    @DisplayName("Площадь оболочки равна площади фигуры")
    @Test
    @Timeout(1)
    void keepsTheAreaOfTheFigure() {
        assertThat("the hull lost a part of the figure",
            new ConvexHull().hull(square()).area(), closeTo(16.0, 1e-9));
    }

    @DisplayName("Две точки оболочки не образуют")
    @Test
    @Timeout(1)
    void takesTwoPointsAsTheyAre() {
        List<Point> points = List.of(new Point(0, 0), new Point(4, 4));
        assertThat("two points suddenly form a polygon",
            new ConvexHull().hull(points).size(), is(2));
    }

    private static List<Point> square() {
        return List.of(new Point(0, 0), new Point(4, 0), new Point(4, 4), new Point(0, 4), new Point(2, 2));
    }
}
