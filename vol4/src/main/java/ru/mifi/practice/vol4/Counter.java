package ru.mifi.practice.vol4;

import java.util.concurrent.atomic.AtomicInteger;

public interface Counter {
    void increment();

    void reset();

    final class Default implements Counter {
        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public void increment() {
            count.incrementAndGet();
        }

        @Override
        public void reset() {
            count.set(0);
        }

        @Override
        public String toString() {
            return String.valueOf(count.intValue());
        }
    }
}
