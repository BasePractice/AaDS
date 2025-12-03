package ru.mifi.practice.voln.jt;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JohnsonTrotter {
    private static final int LEFT = -1;

    private JohnsonTrotter() {
    }

    public static Iterable<int[]> permutations(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Только положительное количество перестановок возможно");
        }
        return () -> new Iterator<>() {
            private final int size = n;
            private final int[] values = initValues(n);
            private final int[] dirs = initDirs(n);
            private boolean first = true;

            @Override
            public boolean hasNext() {
                return first || hasMobile();
            }

            @Override
            public int[] next() {
                if (first) {
                    first = false;
                    return copy(values);
                }
                if (!step()) {
                    throw new NoSuchElementException();
                }
                return copy(values);
            }

            private boolean step() {
                int idx = findLargestMobileIndex();
                if (idx < 0) {
                    return false;
                }
                int moveDir = dirs[idx];
                int swapIdx = idx + moveDir;
                swap(values, idx, swapIdx);
                swap(dirs, idx, swapIdx);
                int movedVal = values[swapIdx];
                for (int i = 0; i < size; i++) {
                    if (values[i] > movedVal) {
                        dirs[i] = -dirs[i];
                    }
                }
                return true;
            }

            private boolean hasMobile() {
                return findLargestMobileIndex() >= 0;
            }

            private int findLargestMobileIndex() {
                int bestIdx = -1;
                int bestVal = -1;
                for (int i = 0; i < size; i++) {
                    int dir = dirs[i];
                    int neighbor = i + dir;
                    if (neighbor < 0 || neighbor >= size) {
                        continue;
                    }
                    if (values[i] > values[neighbor] && values[i] > bestVal) {
                        bestVal = values[i];
                        bestIdx = i;
                    }
                }
                return bestIdx;
            }

            private int[] initValues(int len) {
                int[] a = new int[len];
                for (int i = 0; i < len; i++) {
                    a[i] = i + 1;
                }
                return a;
            }

            private int[] initDirs(int len) {
                int[] d = new int[len];
                Arrays.fill(d, LEFT);
                return d;
            }

            private int[] copy(int[] src) {
                int[] dst = new int[src.length];
                System.arraycopy(src, 0, dst, 0, src.length);
                return dst;
            }

            private void swap(int[] a, int i, int j) {
                int t = a[i];
                a[i] = a[j];
                a[j] = t;
            }
        };
    }
}
