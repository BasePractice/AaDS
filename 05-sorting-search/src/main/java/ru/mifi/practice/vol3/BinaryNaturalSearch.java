package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import java.util.Objects;
import java.util.Optional;

/**
 * Двоичный поиск по монотонной функции на натуральном диапазоне: ищется аргумент, на котором
 * функция даёт заданное значение.
 *
 * <p>Возвращается именно аргумент, как и у поиска по индексу: само значение спрашивающий и так
 * знает — он его передал. Раньше отсюда возвращалось переданное значение, то есть ответ сводился
 * к «нашлось или нет», а найденный аргумент терялся.
 */
public final class BinaryNaturalSearch implements Search<Long, Number> {
    private final boolean debug;

    public BinaryNaturalSearch(boolean debug) {
        this.debug = debug;
    }

    @Override
    public Optional<Number> search(Long element, Range<Long> range, Function<Long> function, Counter counter) {
        var left = range.left;
        var right = range.right;
        if (left >= right) {
            return Optional.empty();
        }
        while (!Objects.equals(left, right - 1)) {
            var mid = (right + left) >>> 1;
            if (debug) {
                System.out.printf("[%2d - %2d]. mid: %d%n", left, right, mid);
            }
            var result = function.apply(mid);
            if (result > element) {
                right = mid;
            } else {
                left = mid;
            }
            counter.increment();
        }
        if (Objects.equals(function.apply(left), element)) {
            return Optional.of(left);
        }
        return Optional.empty();
    }
}
