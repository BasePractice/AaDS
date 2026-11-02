package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.commons.ObjectCounter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Двоичный отражённый код Грея: последовательность из 2^n битовых наборов, в которой соседние
 * наборы отличаются ровно в одном разряде. Код набора с номером i равен i ^ (i &gt;&gt; 1).
 */
public final class GrayCode {
    private static final int MAX_BITS = 30;

    private GrayCode() {
    }

    public static Iterable<int[]> of(int bits) {
        return of(bits, Counter.create());
    }

    public static Iterable<int[]> of(int bits, Counter counter) {
        return of(bits, counter, ObjectCounter.create());
    }

    public static Iterable<int[]> of(int bits, Counter counter, ObjectCounter objects) {
        if (bits < 0 || bits > MAX_BITS) {
            throw new IllegalArgumentException("Количество битов должно быть от нуля до " + MAX_BITS + ": " + bits);
        }
        return () -> new Iterator<>() {
            private final int size = 1 << bits;
            private int index;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public int[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Код Грея закончился");
                }
                counter.increment();
                objects.allocate();
                int value = index ^ (index >>> 1);
                ++index;
                int[] code = new int[bits];
                for (int bit = 0; bit < bits; bit++) {
                    counter.increment();
                    code[bits - bit - 1] = value & 1;
                    value >>>= 1;
                }
                return code;
            }
        };
    }
}
