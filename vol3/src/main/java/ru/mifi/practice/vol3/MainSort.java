package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import java.util.List;

import static ru.mifi.practice.vol3.NumberGenerator.MAX_GENERATED_ELEMENT_VALUE;
import static ru.mifi.practice.vol3.NumberGenerator.generateSlice;

public abstract class MainSort {
    /**
     * ДЗ: почему реальная сложность больше в два раза сложности расчетной?
     */
    public static void main(String[] args) {
        final boolean debug = false;
        List<Integer> slice = generateSlice(10000);
//        List<Integer> slice = generateSlice(100);
//        List<Integer> slice = List.of(7, 8, 2, 0, 5, 2, 7, 0);
        System.out.println("    BAD: " + ((long) slice.size() * slice.size()));
        System.out.println("   MUST: " + Math.round(slice.size() * (Math.log(slice.size()) / Math.log(2))));
        System.out.println("===========");
        for (Algorithms algorithm : Algorithms.values()) {
            Counter counter = new Counter.Default();
            List<Integer> sorted = algorithm.sort(slice, counter, debug);
            System.out.printf("%7s: %s%s%n", algorithm, counter, ordered(sorted) ? "" : " — НЕ ОТСОРТИРОВАНО");
        }
    }

    private static boolean ordered(List<Integer> array) {
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i - 1) > array.get(i)) {
                return false;
            }
        }
        return true;
    }

    enum Algorithms implements Sort<Integer> {
        MERGE(new MergeSort<>()),
        QUICK_H(new QuickSort<>()),
        QUICK_R(new QuickSort<>(new QuickSort.Strategy.Randomly<Integer>())),
        COUNT(new CountSort(MAX_GENERATED_ELEMENT_VALUE)),
        RADIX(new RadixSort()),
        HEAP(new HeapSort<>());

        private final Sort<Integer> sort;

        Algorithms(Sort<Integer> sort) {
            this.sort = sort;
        }

        @Override
        public List<Integer> sort(List<Integer> array, Counter counter, boolean debug) {
            return sort.sort(array, counter, debug);
        }
    }
}
