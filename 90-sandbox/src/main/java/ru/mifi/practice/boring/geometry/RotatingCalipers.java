package ru.mifi.practice.boring.geometry;

import java.util.List;

/**
 * Вращающиеся калиперы: находят диаметр множества точек — наибольшее расстояние между двумя из
 * них. Перебор всех пар стоит O(n²), но искать среди всех точек и не нужно: обе точки диаметра
 * обязательно лежат на выпуклой оболочке, а по оболочке хватает одного круга.
 *
 * <p>Образ такой: к оболочке приставлены две параллельные губки штангенциркуля и медленно
 * поворачиваются вокруг неё. Пара точек, которых они касаются, за полный оборот перебирает все
 * противоположные пары — их O(n), а не O(n²). В коде вращение выглядит как два указателя:
 * первый идёт по вершинам, второй сдвигается вперёд, пока удаляется от текущего ребра.
 *
 * <p>«Удаляется» проверяется площадью треугольника, то есть векторным произведением, — снова
 * только целые числа. Вся цена алгоритма — построение оболочки, O(n log n); сам обход линеен.
 */
public final class RotatingCalipers {

    /** Пара наиболее удалённых друг от друга точек. */
    public List<Point> diameter(List<Point> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Диаметр есть только там, где точек хотя бы две");
        }
        List<Point> hull = new ConvexHull().hull(points).vertices();
        if (hull.size() < 3) {
            return farthest(hull);
        }
        List<Point> diameter = List.of(hull.get(0), hull.get(1));
        int opposite = 1;
        for (int at = 0; at < hull.size(); at++) {
            Point from = hull.get(at);
            Point to = hull.get((at + 1) % hull.size());
            opposite = rotated(hull, from, to, opposite);
            diameter = longer(diameter, List.of(from, hull.get(opposite)));
            diameter = longer(diameter, List.of(to, hull.get(opposite)));
        }
        return diameter;
    }

    /** Сдвиг второй губки вперёд, пока она удаляется от ребра. */
    private int rotated(List<Point> hull, Point from, Point to, int opposite) {
        int moved = opposite;
        while (area(from, to, hull.get((moved + 1) % hull.size())) > area(from, to, hull.get(moved))) {
            moved = (moved + 1) % hull.size();
        }
        return moved;
    }

    private long area(Point from, Point to, Point apex) {
        return Math.abs(from.cross(to, apex));
    }

    private List<Point> longer(List<Point> diameter, List<Point> pair) {
        return squared(pair) > squared(diameter) ? pair : diameter;
    }

    private List<Point> farthest(List<Point> points) {
        List<Point> farthest = List.of(points.get(0), points.get(points.size() - 1));
        for (int at = 0; at < points.size(); at++) {
            for (int other = at + 1; other < points.size(); other++) {
                farthest = longer(farthest, List.of(points.get(at), points.get(other)));
            }
        }
        return farthest;
    }

    private long squared(List<Point> pair) {
        return pair.get(0).squared(pair.get(1));
    }
}
