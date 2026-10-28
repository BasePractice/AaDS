package ru.mifi.practice.boring.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Отрезок по Брезенхему")
final class BresenhamTest {

    @DisplayName("Рисует горизонтальный отрезок")
    @Test
    @Timeout(1)
    void drawsTheHorizontalLine() {
        assertThat("the horizontal line is wrong",
            new Bresenham().line(new Point(0, 0), new Point(3, 0)),
            contains(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0)));
    }

    @DisplayName("Рисует диагональ по одному пикселю на шаг")
    @Test
    @Timeout(1)
    void drawsTheDiagonal() {
        assertThat("the diagonal is wrong",
            new Bresenham().line(new Point(0, 0), new Point(3, 3)),
            contains(new Point(0, 0), new Point(1, 1), new Point(2, 2), new Point(3, 3)));
    }

    @DisplayName("Рисует пологий отрезок без разрывов по горизонтали")
    @Test
    @Timeout(1)
    void drawsTheShallowLine() {
        assertThat("the shallow line has gaps",
            new Bresenham().line(new Point(0, 0), new Point(5, 2)), hasSize(6));
    }

    @DisplayName("Рисует в обратную сторону так же")
    @Test
    @Timeout(1)
    void drawsBackwardsTheSameWay() {
        List<Point> forward = new Bresenham().line(new Point(0, 0), new Point(5, 2));
        assertThat("the line drawn backwards has another length",
            new Bresenham().line(new Point(5, 2), new Point(0, 0)), hasSize(forward.size()));
    }

    @DisplayName("Отрезок из точки в неё саму — один пиксель")
    @Test
    @Timeout(1)
    void drawsASinglePixel() {
        assertThat("a point turned into more than one pixel",
            new Bresenham().line(new Point(2, 2), new Point(2, 2)), is(List.of(new Point(2, 2))));
    }

    @DisplayName("Рисует крутой отрезок вниз и влево")
    @Test
    @Timeout(1)
    void drawsTheSteepLineDownwards() {
        assertThat("the steep line downwards is wrong",
            new Bresenham().line(new Point(0, 0), new Point(-2, -5)), hasSize(6));
    }
}
