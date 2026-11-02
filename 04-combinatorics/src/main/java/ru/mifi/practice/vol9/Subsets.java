package ru.mifi.practice.vol9;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Все подмножества множества из n элементов. Подмножество кодируется битовой маской: единица
 * означает, что элемент с этим индексом входит в подмножество.
 */
public final class Subsets {

    private Subsets() {
    }

    public static Iterable<int[]> of(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Размер множества не может быть отрицательным: " + size);
        }
        return () -> new Iterator<>() {
            private final int[] current = new int[size];
            private boolean has = true;

            @Override
            public boolean hasNext() {
                return has;
            }

            @Override
            public int[] next() {
                if (!has) {
                    throw new NoSuchElementException("Подмножества закончились");
                }
                int[] result = current.clone();
                has = advance(current);
                return result;
            }
        };
    }

    private static boolean advance(int[] mask) {
        int index = mask.length - 1;
        while (index >= 0 && mask[index] == 1) {
            mask[index] = 0;
            --index;
        }
        if (index < 0) {
            return false;
        }
        mask[index] = 1;
        return true;
    }
}
