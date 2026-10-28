package ru.mifi.practice.vol1.sort;

import java.util.Arrays;

/**
 * Стратегия сортировки массива сравнимых элементов на месте. Здесь остались стратегии, которые
 * меряются временем: штатные сортировки JDK и своё слияние. Квадратичные переехали в раздел 5
 * ({@code vol3}), где считают шаги счётчиком и сравниваются с O(n log n) на одних данных.
 */
@FunctionalInterface
public interface Sortable<T extends Comparable<T>> {
    void sort(T[] data);

    enum Type {
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

        private static <T> void swap(T[] array, int first, int second) {
            T value = array[first];
            array[first] = array[second];
            array[second] = value;
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
                    dest[i] = src[p];
                    ++p;
                } else {
                    dest[i] = src[q];
                    ++q;
                }
            }
        }

        @Override
        public void sort(T[] data) {
            T[] aux = data.clone();
            mergeSort(aux, data, 0, data.length, 0);
        }
    }
}
