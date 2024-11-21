package ru.mifi.practice.vol3;

import java.util.Optional;
import java.util.function.UnaryOperator;

public interface Search<E extends Number & Comparable<E>, R extends Number> {
    Optional<R> search(E element, Range<E> range, Function<E> function, Counter counter);

    @FunctionalInterface
    interface Function<E extends Number> extends UnaryOperator<E> {
    }

    final class Range<E extends Number> {
        final E left;
        final E right;

        public Range(E left, E right) {
            this.left = left;
            this.right = right;
        }
    }
}
