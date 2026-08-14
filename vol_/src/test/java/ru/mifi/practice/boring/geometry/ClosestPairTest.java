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

@DisplayName("Ближайшая пара точек")
final class ClosestPairTest {

    @DisplayName("Находит ближайшую пару")
    @Test
    @Timeout(1)
    void findsTheClosestPair() {
        List<Point> points = List.of(
            new Point(0, 0), new Point(10, 10), new Point(11, 11), new Point(30, 5));
        assertThat("the closest pair is wrong",
            new ClosestPair().closest(points), containsInAnyOrder(new Point(10, 10), new Point(11, 11)));
    }

    @DisplayName("Находит пару, разорванную серединой")
    @Test
    @Timeout(1)
    void findsThePairAcrossTheBorder() {
        List<Point> points = List.of(
            new Point(0, 0), new Point(1, 20), new Point(9, 0), new Point(10, 1),
            new Point(19, 20), new Point(20, 0));
        assertThat("the pair split by the dividing line is missed",
            new ClosestPair().closest(points), containsInAnyOrder(new Point(9, 0), new Point(10, 1)));
    }

    @DisplayName("Совпадающие точки — ближайшая пара")
    @Test
    @Timeout(1)
    void findsTheCoincidingPoints() {
        List<Point> points = List.of(new Point(0, 0), new Point(5, 5), new Point(5, 5), new Point(9, 1));
        assertThat("the coinciding points are not the closest pair",
            new ClosestPair().closest(points).get(0).distance(new ClosestPair().closest(points).get(1)),
            is(0.0));
    }

    @DisplayName("Одной точки для пары мало")
    @Test
    @Timeout(1)
    void refusesASinglePoint() {
        assertThat("a single point got a pair",
            assertThrows(IllegalArgumentException.class,
                () -> new ClosestPair().closest(List.of(new Point(0, 0)))).getMessage(),
            is("Ближайшая пара есть только там, где точек хотя бы две"));
    }

    @DisplayName("Находит то же, что полный перебор пар")
    @Test
    @Timeout(10)
    void agreesWithThePlainWalk() {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            List<Point> points = new ArrayList<>();
            for (int i = 0; i < 2 + random.nextInt(60); i++) {
                points.add(new Point(random.nextInt(100), random.nextInt(100)));
            }
            List<Point> closest = new ClosestPair().closest(points);
            if (closest.get(0).squared(closest.get(1)) != brute(points)) {
                ++mismatches;
            }
        }
        assertThat("the divide and conquer search disagrees with the plain walk", mismatches, is(0));
    }

    private static long brute(List<Point> points) {
        long closest = Long.MAX_VALUE;
        for (int at = 0; at < points.size(); at++) {
            for (int other = at + 1; other < points.size(); other++) {
                closest = Math.min(closest, points.get(at).squared(points.get(other)));
            }
        }
        return closest;
    }
}
