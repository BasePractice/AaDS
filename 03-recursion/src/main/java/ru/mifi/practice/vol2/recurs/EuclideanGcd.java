package ru.mifi.practice.vol2.recurs;

import ru.mifi.practice.commons.Counter;

/** Место для решения студента. НОД двух неотрицательных чисел. */
public final class EuclideanGcd {
    /**
     * Находит НОД рекурсией. Для пары (0, 0) возвращает 0.
     * Счётчик увеличивается один раз на каждую операцию остатка.
     *
     * @throws IllegalArgumentException если хотя бы один аргумент отрицателен
     */
    public long recursive(long first, long second, Counter counter) {
        throw new UnsupportedOperationException("Реализуйте рекурсивный алгоритм Евклида");
    }

    /**
     * Находит НОД циклом с тем же контрактом и подсчётом остатков.
     *
     * @throws IllegalArgumentException если хотя бы один аргумент отрицателен
     */
    public long iterative(long first, long second, Counter counter) {
        throw new UnsupportedOperationException("Реализуйте циклический алгоритм Евклида");
    }
}
