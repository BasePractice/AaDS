package ru.mifi.practice.commons;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Счётчик выделенных объектов. В отличие от счётчика операций, он оценивает не время работы, а
 * объём памяти: каждый учтённый объект — это массив, коллекция, строка или другой объект, который
 * алгоритм создал в куче. По значению счётчика для разных n видно, как быстро растёт память.
 */
public interface ObjectCounter {

    static ObjectCounter create() {
        return new Default();
    }

    /**
     * Учесть один выделенный объект.
     */
    void allocate();

    /**
     * Учесть несколько выделенных объектов.
     */
    void allocate(int count);

    void reset();

    int count();

    final class Default implements ObjectCounter {
        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public void allocate() {
            count.incrementAndGet();
        }

        @Override
        public void allocate(int objects) {
            if (objects > 0) {
                count.addAndGet(objects);
            }
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
