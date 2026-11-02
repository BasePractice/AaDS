package ru.mifi.practice.vol9;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Разбиения числа на положительные слагаемые без учёта порядка. Слагаемые идут по невозрастанию,
 * поэтому каждое разбиение встречается ровно один раз.
 */
public final class Partitions {

    private Partitions() {
    }

    public static Iterable<int[]> of(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Число не может быть отрицательным: " + number);
        }
        return () -> new Iterator<>() {
            private int[] current = number == 0 ? new int[0] : new int[]{number};
            private boolean has = true;

            @Override
            public boolean hasNext() {
                return has;
            }

            @Override
            public int[] next() {
                if (!has) {
                    throw new NoSuchElementException("Разбиения закончились");
                }
                int[] result = current;
                int[] next = successor(current);
                has = next.length > 0;
                current = next;
                return result;
            }
        };
    }

    private static int[] successor(int[] parts) {
        int index = parts.length - 1;
        while (index >= 0 && parts[index] <= 1) {
            --index;
        }
        if (index < 0) {
            return new int[0];
        }
        int value = parts[index] - 1;
        int total = 0;
        for (int part : parts) {
            total += part;
        }
        int[] next = new int[total];
        System.arraycopy(parts, 0, next, 0, index);
        next[index] = value;
        int remaining = total;
        for (int i = 0; i <= index; i++) {
            remaining -= next[i];
        }
        int position = index + 1;
        while (remaining > 0) {
            int part = Math.min(value, remaining);
            next[position] = part;
            remaining -= part;
            ++position;
        }
        int[] trimmed = new int[position];
        System.arraycopy(next, 0, trimmed, 0, position);
        return trimmed;
    }
}
