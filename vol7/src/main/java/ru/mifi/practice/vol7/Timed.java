package ru.mifi.practice.vol7;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface Timed<T> {
    T timed(String title, T start, UnaryOperator<T> execution);


    final class Elapsed<T> implements Timed<T> {

        @Override
        public T timed(String title, T init, UnaryOperator<T> execution) {
            long start = System.currentTimeMillis();
            try {
                return execution.apply(init);
            } finally {
                long elapsed = System.currentTimeMillis() - start;
                System.out.println(title + ": " + elapsed + "ms");
            }
        }
    }
}
