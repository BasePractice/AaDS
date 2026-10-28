package ru.mifi.practice.vol2.sudoku;

/** Решатель судоку с подсчётом итераций перебора. */
public interface Sudoku {

    static Factory recursionFactory(boolean debug) {
        return (size, values) -> new RecursionFallback(size, values, debug);
    }

    boolean solve();

    default void print() {
        print("");
    }

    void print(String title);

    int iterations();

    void clear();

    @FunctionalInterface
    interface Factory {
        Sudoku create(int size, int[][] values);
    }
}
