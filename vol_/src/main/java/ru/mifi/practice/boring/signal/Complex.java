package ru.mifi.practice.boring.signal;

/**
 * Комплексное число — структура, на которой держится преобразование Фурье. Действительная часть
 * отвечает за косинус, мнимая за синус, а умножение на комплексное число единичной длины — это
 * поворот на плоскости. Именно поэтому Фурье записывают комплексными числами: иначе пришлось бы
 * тащить синус и косинус двумя отдельными таблицами.
 */
public record Complex(double real, double imaginary) {

    public Complex plus(Complex other) {
        return new Complex(real + other.real, imaginary + other.imaginary);
    }

    public Complex minus(Complex other) {
        return new Complex(real - other.real, imaginary - other.imaginary);
    }

    public Complex times(Complex other) {
        return new Complex(
            real * other.real - imaginary * other.imaginary,
            real * other.imaginary + imaginary * other.real);
    }

    public Complex divided(double divisor) {
        return new Complex(real / divisor, imaginary / divisor);
    }

    /** Поворот на заданный угол — число единичной длины, на которое умножают. */
    public static Complex turn(double angle) {
        return new Complex(Math.cos(angle), Math.sin(angle));
    }

    public double magnitude() {
        return Math.hypot(real, imaginary);
    }

    @Override
    public String toString() {
        return String.format("%.3f%+.3fi", real, imaginary);
    }
}
