package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import java.util.Optional;

/**
 * Двоичный поиск по монотонной функции на вещественном диапазоне: ищется аргумент, на котором
 * функция даёт заданное значение, с точностью до дельты.
 *
 * <p>Дельта задаёт точность по аргументу, а не по значению. Раньше она же служила допуском при
 * сравнении значений, и поиск проваливался там, где функция растёт быстрее единицы: корень из
 * двух с точностью до миллионной не находился вовсе, потому что около него квадрат меняется
 * втрое быстрее аргумента.
 *
 * <p>Поэтому попадание проверяется не допуском на значении, а границами диапазона: если искомое
 * лежит между значениями функции на концах, деление пополам к нему сходится.
 */
public final class BinaryRealSearch implements Search<Double, Number> {
    private final Number delta;
    private final boolean debug;

    public BinaryRealSearch(Number delta, boolean debug) {
        this.delta = delta;
        this.debug = debug;
    }

    @Override
    public Optional<Number> search(Double element, Range<Double> range, Function<Double> function, Counter counter) {
        var left = range.left;
        var right = range.right;
        if (left > right || function.apply(left) > element || function.apply(right) < element) {
            return Optional.empty();
        }
        while (Math.abs(left - right) > delta.doubleValue()) {
            var mid = (right + left) / 2;
            if (debug) {
                System.out.printf("[%5f - %5f]. mid: %f%n", left, right, mid);
            }
            if (function.apply(mid) > element) {
                right = mid;
            } else {
                left = mid;
            }
            counter.increment();
        }
        return Optional.of(left);
    }
}
