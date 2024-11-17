package ru.mifi.practice.vol3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class QuickSort<E extends Comparable<E>> implements Sort<E> {
    @Override
    public List<E> sort(List<E> array, Counter counter, boolean debug) {
        List<E> sortable = new ArrayList<>(array);
        quickSort(sortable, 0, sortable.size() - 1, counter, debug);
        return sortable;
    }

    private void quickSort(List<E> array, int low, int high, Counter counter, boolean debug) {
        if (low >= high - 1) {
            return;
        }
        int middle = part(array, low, high, counter, debug);
        quickSort(array, low, middle, counter, debug);
        quickSort(array, middle + 1, high, counter, debug);
    }

    private int part(List<E> array, int low, int high, Counter counter, boolean debug) {
        E baseElement = array.get(baseIndex(low, high, counter, debug));
        int i = low - 1;
        int j = high;
        while (true) {
            do {
                ++i;
                counter.increment();
            } while (array.get(i).compareTo(baseElement) < 0);
            do {
                --j;
                counter.increment();
            } while (array.get(j).compareTo(baseElement) > 0);
            if (i >= j) {
                return j;
            }
            Collections.swap(array, i, j);
        }
    }

    private int baseIndex(int low, int high, Counter counter, boolean debug) {
        return low + (high - low) / 2;
    }
}
