package ru.mifi.practice.vol6;

import java.util.concurrent.atomic.AtomicInteger;

public interface Counter {
    static Counter create() {
        return new Default();
    }

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
