package ru.mifi.practice.boring.sort;

/**
 * Поразрядная сортировка: числа раскладываются по корзинам не целиком, а по одному разряду за
 * проход, начиная с младшего. Сравнений нет вовсе — есть подсчёт, поэтому нижняя оценка
 * O(n log n) для сортировок сравнением её не касается: цена — O(k·n), где k — число разрядов.
 *
 * <p>Разряд здесь восьмибитный, значит проходов ровно четыре на int. Каждый проход обязан быть
 * устойчивым, иначе порядок, наведённый младшими разрядами, разрушится — поэтому внутри
 * сортировка подсчётом, а не что угодно.
 *
 * <p>Знак учитывается сдвигом: у отрицательных чисел старший бит единица, поэтому в последнем
 * разряде они оказались бы позади. Прибавление 2^31 к каждому числу переносит весь диапазон в
 * неотрицательный, а после сортировки сдвиг снимается.
 */
public final class RadixSort implements Sort {
    private static final int RADIX = 256;
    private static final int DIGITS = 4;

    @Override
    public int[] sorted(int[] values) {
        int[] sorted = shifted(values);
        for (int digit = 0; digit < DIGITS; digit++) {
            sorted = counted(sorted, digit);
        }
        return shifted(sorted);
    }

    private int[] counted(int[] values, int digit) {
        int[] counts = new int[RADIX + 1];
        for (int value : values) {
            counts[digit(value, digit) + 1]++;
        }
        for (int bucket = 1; bucket <= RADIX; bucket++) {
            counts[bucket] += counts[bucket - 1];
        }
        int[] sorted = new int[values.length];
        for (int value : values) {
            int bucket = digit(value, digit);
            sorted[counts[bucket]] = value;
            counts[bucket]++;
        }
        return sorted;
    }

    private int digit(int value, int digit) {
        return value >>> digit * 8 & RADIX - 1;
    }

    private int[] shifted(int[] values) {
        int[] shifted = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            shifted[i] = values[i] ^ Integer.MIN_VALUE;
        }
        return shifted;
    }
}
