package ru.mifi.practice.boring.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Алгоритм Брезенхема: рисует отрезок на решётке пикселей, не сделав ни одного деления и ни
 * одной операции с дробями. Наивный способ считал бы y = kx + b для каждого x — это умножение и
 * округление на каждый пиксель, а на технике 1962 года, для которой алгоритм и придумывался,
 * дробная арифметика была роскошью.
 *
 * <p>Вместо координаты хранится ошибка — накопленное отклонение проведённой линии от идеальной.
 * На каждом шаге ошибка меняется на целую величину, и когда она переваливает за половину
 * пикселя, координата сдвигается, а ошибка корректируется. Все действия — сложение и сравнение
 * целых.
 *
 * <p>Здесь взята форма, работающая сразу во всех восьми октантах: приращения берутся по модулю,
 * направление хранится знаком, поэтому не нужно ни разбирать случаи, ни зеркалить отрезок.
 */
public final class Bresenham {

    /** Пиксели отрезка от начала до конца включительно, в порядке рисования. */
    public List<Point> line(Point from, Point to) {
        int dx = Math.abs(to.x() - from.x());
        int dy = -Math.abs(to.y() - from.y());
        int stepX = from.x() < to.x() ? 1 : -1;
        int stepY = from.y() < to.y() ? 1 : -1;
        int error = dx + dy;
        List<Point> pixels = new ArrayList<>();
        int x = from.x();
        int y = from.y();
        while (true) {
            pixels.add(new Point(x, y));
            if (x == to.x() && y == to.y()) {
                return pixels;
            }
            int doubled = error * 2;
            if (doubled >= dy) {
                error += dy;
                x += stepX;
            }
            if (doubled <= dx) {
                error += dx;
                y += stepY;
            }
        }
    }
}
