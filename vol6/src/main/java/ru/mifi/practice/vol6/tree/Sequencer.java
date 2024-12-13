package ru.mifi.practice.vol6.tree;

import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
public interface Sequencer<T> {
    T next();

    final class DefaultInteger implements Sequencer<Integer> {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Integer next() {
            return counter.getAndIncrement();
        }
    }
}
