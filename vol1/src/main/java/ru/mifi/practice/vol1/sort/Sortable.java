package ru.mifi.practice.vol1.sort;

import java.util.Arrays;

@FunctionalInterface
public interface Sortable<T extends Comparable<T>> {
    void sort(T[] data);

    enum Type {
        BUBBLE {
            @Override
            public <T extends Comparable<T>> Sortable<T> newSorting() {
                return new BubbleSorting<>();
            }
        },
        SELECTION {
            @Override
            public <T extends Comparable<T>> Sortable<T> newSorting() {
                return new SelectionSorting<>();
            }
        },
        PARALLEL {
            @Override
            public <T extends Comparable<T>> Sortable<T> newSorting() {
                return new ParallelSorting<>();
            }
        },
        MERGE {
            @Override
            public <T extends Comparable<T>> Sortable<T> newSorting() {
                return new MergeSorting<>();
            }
        },
        QSORT {
            @Override
            public <T extends Comparable<T>> Sortable<T> newSorting() {
                return new QuickSorting<>();
            }
        };

        public abstract <T extends Comparable<T>> Sortable<T> newSorting();

        @Override
        public String toString() {
            return String.format("%9s", this.name());
        }
    }

    final class QuickSorting<T extends Comparable<T>> implements Sortable<T> {
        private QuickSorting() {
        }

        @Override
        public void sort(T[] data) {
            Arrays.sort(data);
        }
    }

    final class ParallelSorting<T extends Comparable<T>> implements Sortable<T> {
        private ParallelSorting() {
        }

        @Override
        public void sort(T[] data) {
            Arrays.parallelSort(data);
        }
    }

    final class MergeSorting<T extends Comparable<T>> implements Sortable<T> {

        private MergeSorting() {
        }

        private static <T> void swap(T[] x, int a, int b) {
            T t = x[a];
            x[a] = x[b];
            x[b] = t;
        }

        private static <T extends Comparable<T>> void mergeSort(T[] src,
                                                                T[] dest,
                                                                int low,
                                                                int high,
                                                                int off) {
            int length = high - low;
            if (length < 7) {
                for (int i = low; i < high; i++) {
                    for (int j = i; j > low
                        && dest[j - 1].compareTo(dest[j]) > 0; j--) {
                        swap(dest, j, j - 1);
                    }
                }
                return;
            }
            final int destLow = low;
            final int destHigh = high;
            low += off;
            high += off;
            int mid = (low + high) >>> 1;
            mergeSort(dest, src, low, mid, -off);
            mergeSort(dest, src, mid, high, -off);
            if (src[mid - 1].compareTo(src[mid]) <= 0) {
                System.arraycopy(src, low, dest, destLow, length);
                return;
            }

            for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
                if (q >= high || p < mid && src[p].compareTo(src[q]) <= 0) {
                    dest[i] = src[p++];
                } else {
                    dest[i] = src[q++];
                }
            }
        }

        @Override
        public void sort(T[] data) {
            T[] aux = data.clone();
            mergeSort(aux, data, 0, data.length, 0);
        }
    }

    final class BubbleSorting<T extends Comparable<T>> implements Sortable<T> {
        private BubbleSorting() {
        }

        @Override
        public void sort(T[] data) {
            for (int i = 0; i < data.length - 1; i++) {
                for (int j = 0; j < data.length - i - 1; j++) {
                    if (data[j].compareTo(data[j + 1]) > 0) {
                        T temp = data[j];
                        data[j] = data[j + 1];
                        data[j + 1] = temp;
                    }
                }
            }
        }
    }

    final class SelectionSorting<T extends Comparable<T>> implements Sortable<T> {
        private SelectionSorting() {
        }

        @Override
        public void sort(T[] data) {
            int minimum;
            for (int i = 0; i < data.length - 1; i++) {
                minimum = i;
                for (int j = i + 1; j < data.length; j++) {
                    if (data[minimum].compareTo(data[j]) > 0) {
                        minimum = j;
                    }
                }
                T temp = data[minimum];
                data[minimum] = data[i];
                data[i] = temp;
            }
        }
    }
}
