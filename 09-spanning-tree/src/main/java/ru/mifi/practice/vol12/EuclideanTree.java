package ru.mifi.practice.vol12;

import java.util.List;

/**
 * Заготовка к заданию слайда 584: минимальное остовное дерево точек плоскости, где вес ребра —
 * евклидово расстояние. Граф полный, и полный перебор пар даёт n(n−1)/2 рёбер — для тысячи
 * точек почти полмиллиона. Прим на матрице расстояний обходится O(n²) без списка рёбер; ещё
 * быстрее — рассматривать только рёбра триангуляции Делоне, их O(n).
 */
public interface EuclideanTree {

    /**
     * Суммарная длина рёбер дерева.
     */
    double length(List<Point> points);

    /**
     * Точка плоскости.
     */
    record Point(double x, double y) {
    }

    /**
     * Место для решения студента.
     */
    final class Plane implements EuclideanTree {

        @Override
        public double length(List<Point> points) {
            throw new UnsupportedOperationException("Задание слайда 584 ещё не выполнено");
        }
    }
}
