package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Линейный поиск перебором: элементы проверяются по очереди слева направо. Порядок в списке не
 * нужен, зато в худшем случае просматривается весь список — O(n) против O(log n) у бинарного.
 */
public final class LinearSearch implements Search<Integer, Number> {
    private final List<Integer> array;

    public LinearSearch(List<Integer> array) {
        this.array = array;
    }

    public Optional<Number> search(Integer element, Counter counter) {
        return search(element, new Range<>(0, array.size()), array::get, counter);
    }

    @Override
    public Optional<Number> search(Integer element, Range<Integer> range, Function<Integer> function, Counter counter) {
        for (int index = range.left; index < range.right; index++) {
            counter.increment();
            if (Objects.equals(function.apply(index), element)) {
                return Optional.of(index);
            }
        }
        return Optional.empty();
    }
}
