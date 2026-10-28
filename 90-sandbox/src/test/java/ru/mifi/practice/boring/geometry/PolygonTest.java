package ru.mifi.practice.boring.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

@DisplayName("Площадь многоугольника формулой шнурка")
final class PolygonTest {

    @DisplayName("Считает площадь квадрата")
    @Test
    @Timeout(1)
    void countsTheAreaOfASquare() {
        assertThat("the area of the square is wrong", square().area(), closeTo(16.0, 1e-9));
    }

    @DisplayName("Считает площадь треугольника с половиной")
    @Test
    @Timeout(1)
    void countsTheHalfOfATriangle() {
        Polygon triangle = new Polygon(List.of(new Point(0, 0), new Point(3, 0), new Point(0, 2)));
        assertThat("the area of the triangle is wrong", triangle.area(), closeTo(3.0, 1e-9));
    }

    @DisplayName("Считает площадь невыпуклого многоугольника")
    @Test
    @Timeout(1)
    void countsTheAreaOfAConcavePolygon() {
        Polygon arrow = new Polygon(List.of(
            new Point(0, 0), new Point(4, 0), new Point(2, 2), new Point(4, 4), new Point(0, 4)));
        assertThat("the area of the concave polygon is wrong", arrow.area(), closeTo(12.0, 1e-9));
    }

    @DisplayName("Направление обхода не меняет площадь")
    @Test
    @Timeout(1)
    void keepsTheAreaWhenReversed() {
        Polygon clockwise = new Polygon(List.of(
            new Point(0, 0), new Point(0, 4), new Point(4, 4), new Point(4, 0)));
        assertThat("the direction of the walk changes the area",
            clockwise.area(), closeTo(square().area(), 1e-9));
    }

    @DisplayName("Обход против часовой стрелки виден по знаку")
    @Test
    @Timeout(1)
    void tellsTheDirectionOfTheWalk() {
        assertThat("the counterclockwise walk is not recognized", square().counterclockwise(), is(true));
    }

    private static Polygon square() {
        return new Polygon(List.of(new Point(0, 0), new Point(4, 0), new Point(4, 4), new Point(0, 4)));
    }
}
