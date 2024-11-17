package ru.mifi.practice.vol3;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
public interface Sort<E extends Comparable<E>> {
    List<E> sort(List<E> array, Counter counter, boolean debug);

    interface Counter {
        void increment();

        final class Default implements Counter {
            private final AtomicInteger count = new AtomicInteger(0);

            @Override
            public void increment() {
                count.incrementAndGet();
            }

            @Override
            public String toString() {
                return String.valueOf(count.intValue());
            }
        }
    }
}
