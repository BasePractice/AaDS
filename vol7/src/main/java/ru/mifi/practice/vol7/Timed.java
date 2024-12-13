package ru.mifi.practice.vol7;

import java.util.function.Function;

@FunctionalInterface
public interface Timed<T, I> {
    T timed(String title, I args, Function<I, T> execution);


    final class Elapsed<T, I> implements Timed<T, I> {

        @Override
        public T timed(String title, I args, Function<I, T> execution) {
            long start = System.currentTimeMillis();
            try {
                return execution.apply(args);
            } finally {
                long elapsed = System.currentTimeMillis() - start;
                System.out.println(title + ": " + elapsed + "ms");
            }
        }
    }
}
