package ru.mifi.practice.vol9;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Перебор перестановок алгоритмом Хипа: на каждом шаге меняются местами два элемента, а состояние
 * перебора хранится в массиве счётчиков. Работает за O(n!) обменов и не требует рекурсии.
 */
public final class HeapPermutations {

    private HeapPermutations() {
    }

    public static Iterable<int[]> of(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Размер перестановки должен быть положительным: " + size);
        }
        return () -> new Iterator<>() {
            private final int[] values = initial(size);
            private final int[] counters = new int[size];
            private final int length = size;
            private int index = 1;
            private int[] next = values.clone();
            private boolean has = true;

            @Override
            public boolean hasNext() {
                return has;
            }

            @Override
            public int[] next() {
                if (!has) {
                    throw new NoSuchElementException("Перестановки закончились");
                }
                int[] result = next;
                has = advance();
                if (has) {
                    next = values.clone();
                }
                return result;
            }

            private boolean advance() {
                while (index < length) {
                    if (counters[index] < index) {
                        if (index % 2 == 0) {
                            swap(values, 0, index);
                        } else {
                            swap(values, counters[index], index);
                        }
                        ++counters[index];
                        index = 1;
                        return true;
                    }
                    counters[index] = 0;
                    ++index;
                }
                return false;
            }
        };
    }

    private static int[] initial(int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = i + 1;
        }
        return values;
    }

    private static void swap(int[] values, int left, int right) {
        int tmp = values[left];
        values[left] = values[right];
        values[right] = tmp;
    }
}
