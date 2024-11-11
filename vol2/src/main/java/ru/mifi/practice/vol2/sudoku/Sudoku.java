package ru.mifi.practice.vol2.sudoku;

public interface Sudoku {

    static Sudoku recursionFallback(int size, int[][] values) {
        return new RecursionFallback(size, values);
    }

    static Factory recursionFactory() {
        return RecursionFallback::new;
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
