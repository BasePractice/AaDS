package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import java.util.ArrayList;
import java.util.List;

/** Сортировка целых чисел подсчётом вхождений. */
public final class CountSort implements Sort<Integer> {
    private final int maxElement;

    public CountSort(int maxElement) {
        this.maxElement = maxElement;
    }

    @Override
    public List<Integer> sort(List<Integer> array, Counter counter, boolean debug) {
        List<Integer> result = new ArrayList<>(array.size());
        int[] filter = new int[maxElement + 1];
        for (Integer element : array) {
            ++filter[element];
            counter.increment();
        }
        for (int val = 0; val <= maxElement; val++) {
            for (int i = 0; i < filter[val]; i++) {
                result.add(val);
                counter.increment();
            }
        }
        return result;
    }
}
