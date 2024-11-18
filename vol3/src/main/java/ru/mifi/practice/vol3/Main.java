package ru.mifi.practice.vol3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public abstract class Main {
    private static final int MAX_GENERATED_ELEMENT_VALUE = 100;

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static List<Integer> generateSlice(int length) {
        Random r = new Random(new Date().getTime());
        List<Integer> slice = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            slice.add(r.nextInt(MAX_GENERATED_ELEMENT_VALUE + 1));
        }
        return slice;
    }

    /**
     * ДЗ: почему реальная сложность больше в два раза сложности расчетной?
     */
    public static void main(String[] args) {
        final boolean debug = false;
//        List<Integer> slice = generateSlice(10000);
        List<Integer> slice = List.of(7, 8, 2, 0, 5, 2, 7, 0);
        System.out.println("    BAD: " + (slice.size() * slice.size()));
        System.out.println("   MUST: " + Math.round(slice.size() * (Math.log(slice.size()) / Math.log(2))));
        System.out.println("===========");
        for (Algorithms algorithm : Algorithms.values()) {
            Sort.Counter counter = new Sort.Counter.Default();
            algorithm.sort(slice, counter, debug);
            System.out.printf("%7s: %s%n", algorithm, counter);
        }
    }

    enum Algorithms implements Sort<Integer> {
        MERGE(new MergeSort<>()),
        QUICK_H(new QuickSort<>()),
        QUICK_R(new QuickSort<>(new QuickSort.Strategy.Randomly<Integer>())),
        COUNT(new CountSort(MAX_GENERATED_ELEMENT_VALUE));

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
