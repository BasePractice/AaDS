package ru.mifi.practice.boring.geometry;

/**
 * Точка на целочисленной решётке — общая структура для всей геометрии пакета. Координаты целые
 * намеренно: тогда векторное произведение и сравнения точны, и не нужно ни epsilon, ни разговоров
 * про то, что 0.1 + 0.2 не равно 0.3. Все алгоритмы здесь обходятся сложением и умножением целых,
 * а корень появляется только там, где просят именно расстояние.
 */
public record Point(int x, int y) {

    /**
     * Векторное произведение векторов из этой точки в две другие. Знак отвечает на вопрос, куда
     * поворачивает путь: больше нуля — налево, меньше — направо, ноль — точки на одной прямой.
     * На этом знаке держатся и выпуклая оболочка, и калиперы.
     */
    public long cross(Point left, Point right) {
        return (long) (left.x - x) * (right.y - y) - (long) (left.y - y) * (right.x - x);
    }

    /** Квадрат расстояния: сравнивать расстояния можно и без корня, точнее и быстрее. */
    public long squared(Point other) {
        return (long) (x - other.x) * (x - other.x) + (long) (y - other.y) * (y - other.y);
    }

    public double distance(Point other) {
        return Math.sqrt(squared(other));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
