package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.commons.ObjectCounter;

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
        return of(number, Counter.create());
    }

    public static Iterable<int[]> of(int number, Counter counter) {
        return of(number, counter, ObjectCounter.create());
    }

    public static Iterable<int[]> of(int number, Counter counter, ObjectCounter objects) {
        if (number < 0) {
            throw new IllegalArgumentException("Число не может быть отрицательным: " + number);
        }
        return () -> {
            objects.allocate();
            return new Iterator<>() {
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
                    counter.increment();
                    int[] result = current;
                    int[] next = successor(current, counter, objects);
                    has = next.length > 0;
                    current = next;
                    return result;
                }
            };
        };
    }

    private static int[] successor(int[] parts, Counter counter, ObjectCounter objects) {
        int index = parts.length - 1;
        while (index >= 0 && parts[index] <= 1) {
            counter.increment();
            --index;
        }
        if (index < 0) {
            objects.allocate();
            return new int[0];
        }
        final int value = parts[index] - 1;
        int total = 0;
        for (int part : parts) {
            counter.increment();
            total += part;
        }
        objects.allocate();
        int[] next = new int[total];
        System.arraycopy(parts, 0, next, 0, index);
        next[index] = value;
        int remaining = total;
        for (int i = 0; i <= index; i++) {
            counter.increment();
            remaining -= next[i];
        }
        int position = index + 1;
        while (remaining > 0) {
            counter.increment();
            int part = Math.min(value, remaining);
            next[position] = part;
            remaining -= part;
            ++position;
        }
        objects.allocate();
        int[] trimmed = new int[position];
        System.arraycopy(next, 0, trimmed, 0, position);
        return trimmed;
    }
}
