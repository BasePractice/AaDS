package ru.mifi.practice.boring.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Диаметр множества точек вращающимися калиперами")
final class RotatingCalipersTest {

    @DisplayName("Диаметр квадрата — его диагональ")
    @Test
    @Timeout(1)
    void takesTheDiagonalOfTheSquare() {
        List<Point> points = List.of(new Point(0, 0), new Point(4, 0), new Point(4, 4), new Point(0, 4));
        assertThat("the diameter of the square is not its diagonal",
            new RotatingCalipers().diameter(points).get(0).squared(
                new RotatingCalipers().diameter(points).get(1)), is(32L));
    }

    @DisplayName("Внутренние точки диаметр не образуют")
    @Test
    @Timeout(1)
    void ignoresTheInnerPoints() {
        List<Point> points = List.of(
            new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10),
            new Point(5, 5), new Point(4, 6));
        assertThat("an inner point got into the diameter",
            new RotatingCalipers().diameter(points),
            containsInAnyOrder(new Point(0, 0), new Point(10, 10)));
    }

    @DisplayName("Диаметр двух точек — они сами")
    @Test
    @Timeout(1)
    void takesTwoPointsAsTheyAre() {
        List<Point> points = List.of(new Point(0, 0), new Point(3, 4));
        assertThat("two points dont form their own diameter",
            new RotatingCalipers().diameter(points), containsInAnyOrder(new Point(0, 0), new Point(3, 4)));
    }

    @DisplayName("Одной точки для диаметра мало")
    @Test
    @Timeout(1)
    void refusesASinglePoint() {
        assertThat("a single point got a diameter",
            assertThrows(IllegalArgumentException.class,
                () -> new RotatingCalipers().diameter(List.of(new Point(1, 1)))).getMessage(),
            is("Диаметр есть только там, где точек хотя бы две"));
    }

    @DisplayName("Находит то же, что полный перебор пар")
    @Test
    @Timeout(10)
    void agreesWithThePlainWalk() {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            List<Point> points = new ArrayList<>();
            for (int i = 0; i < 2 + random.nextInt(40); i++) {
                points.add(new Point(random.nextInt(100), random.nextInt(100)));
            }
            List<Point> diameter = new RotatingCalipers().diameter(points);
            if (diameter.get(0).squared(diameter.get(1)) != brute(points)) {
                ++mismatches;
            }
        }
        assertThat("the calipers disagree with the plain walk", mismatches, is(0));
    }

    private static long brute(List<Point> points) {
        long farthest = 0;
        for (int at = 0; at < points.size(); at++) {
            for (int other = at + 1; other < points.size(); other++) {
                farthest = Math.max(farthest, points.get(at).squared(points.get(other)));
            }
        }
        return farthest;
    }
}
