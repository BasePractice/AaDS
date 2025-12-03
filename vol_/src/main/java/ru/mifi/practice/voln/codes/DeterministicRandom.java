package ru.mifi.practice.voln.codes;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Детерминированный генератор псевдослучайных чисел на базе SplitMix64.
 * Подходит для воспроизводимого выбора соседей по заданному зерну.
 */
public final class DeterministicRandom {
    private static final long GOLDEN_GAMMA = 0x9E3779B97F4A7C15L; // 2^64 / golden ratio

    private final AtomicLong state;

    /**
     * Создаёт генератор c заданным зерном.
     * @param seed зерно
     */
    public DeterministicRandom(long seed) {
        this.state = new AtomicLong(seed);
    }

    /**
     * Следующее 64-битное случайное число.
     * @return значение long
     */
    public long nextLong() {
        long z = state.getAndAdd(GOLDEN_GAMMA);
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /**
     * Случайное число в диапазоне [0, bound).
     * @param bound верхняя граница (эксклюзивно)
     * @return значение от 0 до bound-1
     */
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound должен быть > 0");
        }
        // Используем только 31 младший бит, чтобы избежать отрицательных значений при приведении к int
        int v = (int) (nextLong() & 0x7FFFFFFFL);
        return v % bound;
    }

    /**
     * Случайное double в диапазоне [0, 1).
     * @return значение
     */
    public double nextDouble() {
        long bits = nextLong();
        // 53 случайных бит на мантиссу
        long mantissa = (bits >>> 11) & ((1L << 53) - 1);
        return mantissa / (double) (1L << 53);
    }
}
