package ru.mifi.practice.boring.sort;

import java.util.Arrays;

/**
 * Битонная сортировка — сортирующая сеть: последовательность сравнений здесь не зависит от
 * данных, она известна заранее. Обычная сортировка решает, что сравнивать, по ходу дела, и
 * поэтому плохо ложится на видеокарту или конвейер; сеть же можно выполнять параллельно —
 * сравнения внутри одного слоя независимы.
 *
 * <p>Битонная последовательность сначала возрастает, потом убывает. Алгоритм строит её из двух
 * половин, отсортированных в разные стороны, а затем «схлопывает» слияниями: сравнение элементов
 * на расстоянии половины длины делит битонную последовательность на две битонные, каждая из
 * которых целиком не больше другой.
 *
 * <p>Сеть требует длины, равной степени двойки, поэтому массив дополняется бесконечностями и
 * обрезается обратно. Сравнений O(n log²n) — больше, чем у слияния, но их можно делать разом.
 */
public final class BitonicSort implements Sort {

    @Override
    public int[] sorted(int[] values) {
        int[] padded = padded(values);
        merge(padded, 0, padded.length, true);
        return Arrays.copyOf(padded, values.length);
    }

    private void merge(int[] values, int from, int length, boolean ascending) {
        if (length <= 1) {
            return;
        }
        int half = length / 2;
        merge(values, from, half, true);
        merge(values, from + half, half, false);
        halve(values, from, length, ascending);
    }

    private void halve(int[] values, int from, int length, boolean ascending) {
        if (length <= 1) {
            return;
        }
        int half = length / 2;
        for (int i = from; i < from + half; i++) {
            if (values[i] > values[i + half] == ascending) {
                swap(values, i, i + half);
            }
        }
        halve(values, from, half, ascending);
        halve(values, from + half, half, ascending);
    }

    private int[] padded(int[] values) {
        int length = 1;
        while (length < values.length) {
            length *= 2;
        }
        int[] padded = new int[length];
        Arrays.fill(padded, Integer.MAX_VALUE);
        System.arraycopy(values, 0, padded, 0, values.length);
        return padded;
    }

    private void swap(int[] values, int left, int right) {
        int value = values[left];
        values[left] = values[right];
        values[right] = value;
    }
}
