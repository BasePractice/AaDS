package ru.mifi.practice.boring.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Выпуклая оболочка методом монотонной цепочки Эндрю — наименьший выпуклый многоугольник,
 * содержащий все точки. Резиновая лента, отпущенная вокруг гвоздей.
 *
 * <p>Точки сортируются по координатам, а затем двумя проходами строятся нижняя и верхняя цепочки.
 * В каждом проходе точка добавляется в конец, и пока последние три образуют правый поворот,
 * средняя выбрасывается — она внутри оболочки. Проверка поворота — знак векторного произведения,
 * то есть только целочисленное умножение.
 *
 * <p>От алгоритма Грэхема отличается тем, что не нужно ни выбирать опорную точку, ни сортировать
 * по углу — сортировка обычная, лексикографическая. Цена та же O(n log n), вся она в сортировке:
 * проходы линейны, потому что каждая точка добавляется и выбрасывается не больше одного раза.
 */
public final class ConvexHull {

    /** Вершины оболочки против часовой стрелки, начиная с самой левой нижней. */
    public Polygon hull(List<Point> points) {
        if (points.size() < 3) {
            return new Polygon(points);
        }
        List<Point> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparingInt(Point::x).thenComparingInt(Point::y));
        List<Point> backwards = new ArrayList<>(sorted);
        Collections.reverse(backwards);
        List<Point> lower = chain(sorted);
        List<Point> upper = chain(backwards);
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);
        return new Polygon(lower);
    }

    /** Одна цепочка: точки добавляются, правые повороты выбрасываются. */
    private List<Point> chain(List<Point> sorted) {
        List<Point> chain = new ArrayList<>();
        for (Point point : sorted) {
            while (chain.size() >= 2 && turnsRight(chain, point)) {
                chain.remove(chain.size() - 1);
            }
            chain.add(point);
        }
        return chain;
    }

    private boolean turnsRight(List<Point> chain, Point point) {
        Point last = chain.get(chain.size() - 1);
        Point before = chain.get(chain.size() - 2);
        return before.cross(last, point) <= 0;
    }
}
