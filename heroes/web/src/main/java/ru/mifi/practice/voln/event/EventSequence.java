package ru.mifi.practice.voln.event;

import java.util.concurrent.atomic.AtomicLong;

/** Монотонный генератор порядковых номеров событий. */
public interface EventSequence {
    long next();

    final class Default implements EventSequence {
        private final AtomicLong counter = new AtomicLong(0);

        @Override
        public long next() {
            return counter.incrementAndGet();
        }
    }
}
