package ru.mifi.practice.boring.geometry;

import java.util.List;

/**
 * Многоугольник — список вершин в порядке обхода. Здесь же живёт формула шнурка: площадь любого
 * простого многоугольника считается одним циклом по вершинам, без разбиения на треугольники и
 * без всякой тригонометрии.
 *
 * <p>Название от того, как выглядит запись: координаты выписывают в два столбца и перемножают
 * крест-накрест, будто шнуруют ботинок. По сути это сумма векторных произведений, то есть сумма
 * площадей треугольников от начала координат до каждого ребра: те, что «снаружи», входят со
 * знаком минус и сокращаются.
 *
 * <p>Знак суммы говорит о направлении обхода: против часовой стрелки — плюс, по часовой — минус.
 * Площадь берётся по модулю, а половина появляется потому, что векторное произведение даёт
 * площадь параллелограмма, а не треугольника.
 */
public final class Polygon {
    private final List<Point> vertices;

    public Polygon(List<Point> vertices) {
        this.vertices = List.copyOf(vertices);
    }

    /** Площадь по формуле шнурка. */
    public double area() {
        return Math.abs(doubled()) / 2.0;
    }

    /** Обход против часовой стрелки, если удвоенная площадь положительна. */
    public boolean counterclockwise() {
        return doubled() > 0;
    }

    public List<Point> vertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    /** Удвоенная площадь со знаком — то самое суммирование крест-накрест. */
    private long doubled() {
        long doubled = 0;
        for (int at = 0; at < vertices.size(); at++) {
            Point current = vertices.get(at);
            Point next = vertices.get((at + 1) % vertices.size());
            doubled += (long) current.x() * next.y() - (long) next.x() * current.y();
        }
        return doubled;
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
