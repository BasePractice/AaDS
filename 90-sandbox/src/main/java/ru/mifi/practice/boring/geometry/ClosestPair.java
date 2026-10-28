package ru.mifi.practice.boring.geometry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ближайшая пара точек методом «разделяй и властвуй». Перебор всех пар стоит O(n²); здесь
 * плоскость делится вертикальной прямой пополам, в каждой половине задача решается рекурсивно, и
 * остаётся проверить пары, разорванные этой прямой.
 *
 * <p>Вся соль в полосе: если в половинах ближайшее расстояние равно d, то разорванная пара может
 * оказаться ближе, только когда обе её точки лежат в полосе шириной d вокруг разделяющей прямой.
 * Точки полосы перебираются по возрастанию ординаты, и каждой хватает сравнения с несколькими
 * следующими: дальше ординаты отличаются больше чем на d, и смотреть уже незачем. Отсюда и
 * линейность разбора полосы — в ней просто не может быть много точек, попарно далёких друг от
 * друга.
 *
 * <p>Полоса здесь сортируется на каждом уровне рекурсии, поэтому цена O(n log²n). Классическая
 * реализация сортирует по ординате один раз и тащит порядок сквозь рекурсию, получая O(n log n),
 * но теряет в наглядности ровно то, ради чего этот алгоритм и разбирают.
 *
 * <p>Расстояния сравниваются квадратами: корень ничего не меняет в порядке, зато вносит
 * погрешность и стоит времени.
 */
public final class ClosestPair {

    /** Пара ближайших точек; при равенстве расстояний — любая из ближайших. */
    public List<Point> closest(List<Point> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Ближайшая пара есть только там, где точек хотя бы две");
        }
        List<Point> byX = new ArrayList<>(points);
        byX.sort(Comparator.comparingInt(Point::x).thenComparingInt(Point::y));
        return divided(byX);
    }

    private List<Point> divided(List<Point> byX) {
        if (byX.size() <= 3) {
            return brute(byX);
        }
        int middle = byX.size() / 2;
        int border = byX.get(middle).x();
        List<Point> left = divided(byX.subList(0, middle));
        List<Point> right = divided(byX.subList(middle, byX.size()));
        List<Point> best = squared(left) <= squared(right) ? left : right;
        return strip(byX, border, best);
    }

    /** Разбор полосы: соседи по ординате, пока разница ординат меньше найденного расстояния. */
    private List<Point> strip(List<Point> byX, int border, List<Point> best) {
        List<Point> strip = new ArrayList<>();
        for (Point point : byX) {
            if (squared(point.x() - border) < squared(best)) {
                strip.add(point);
            }
        }
        strip.sort(Comparator.comparingInt(Point::y));
        List<Point> closest = best;
        for (int at = 0; at < strip.size(); at++) {
            closest = nearer(strip, at, closest);
        }
        return closest;
    }

    private List<Point> nearer(List<Point> strip, int at, List<Point> closest) {
        List<Point> nearer = closest;
        for (int other = at + 1; other < strip.size(); other++) {
            if (squared(strip.get(other).y() - strip.get(at).y()) >= squared(nearer)) {
                return nearer;
            }
            if (strip.get(at).squared(strip.get(other)) < squared(nearer)) {
                nearer = List.of(strip.get(at), strip.get(other));
            }
        }
        return nearer;
    }

    private List<Point> brute(List<Point> points) {
        List<Point> closest = List.of(points.get(0), points.get(1));
        for (int at = 0; at < points.size(); at++) {
            for (int other = at + 1; other < points.size(); other++) {
                if (points.get(at).squared(points.get(other)) < squared(closest)) {
                    closest = List.of(points.get(at), points.get(other));
                }
            }
        }
        return closest;
    }

    private long squared(List<Point> pair) {
        return pair.get(0).squared(pair.get(1));
    }

    private long squared(int length) {
        return (long) length * length;
    }
}
