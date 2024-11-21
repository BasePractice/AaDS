package ru.mifi.practice.vol3;

import java.util.Optional;

public final class BinaryRealSearch implements Search<Double, Number> {
    private final Number delta;

    public BinaryRealSearch(Number delta) {
        this.delta = delta;
    }

    @Override
    public Optional<Number> search(Double element, Range<Double> range, Function<Double> function, Counter counter) {
        var left = range.left;
        var right = range.right;
        while (Math.abs(left - right) > delta.doubleValue()) {
            var mid = (right + left) / 2;
            var result = function.apply(mid);
            if (result > element) {
                right = mid;
            } else {
                left = mid;
            }
            counter.increment();
        }
        var result = function.apply(left);
        if (Math.abs(result - element) <= delta.doubleValue()) {
            return Optional.of(result);
        }
        return Optional.empty();
    }
}
