package ru.mifi.practice.commons;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Счётчик элементарных операций алгоритма. Нужен, чтобы сравнивать реализации по фактическому
 * числу шагов, а не по времени, которое зависит от машины и текущей нагрузки, — как в разделе 1
 * курса. Значение читается методом count, поэтому его можно проверить в тесте, а не только
 * напечатать.
 */
public interface Counter {

    static Counter create() {
        return new Default();
    }

    void increment();

    void reset();

    int count();

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
        public int count() {
            return count.intValue();
        }

        @Override
        public String toString() {
            return String.valueOf(count.intValue());
        }
    }
}
