package ru.mifi.practice.vol3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MergeSort<E extends Comparable<E>> implements Sort<E> {
    public static <E extends Comparable<E>> List<E> merge(List<E> left, List<E> right, Counter counter, boolean debug) {
        List<E> result = new ArrayList<>(right.size() + left.size());
        int l = 0;
        int r = 0;
        while (l < left.size() || r < right.size()) {
            if (l < left.size() && (r == right.size() || left.get(l).compareTo(right.get(r)) < 0)) {
                result.add(left.get(l));
                ++l;
            } else {
                result.add(right.get(r));
                ++r;
            }
            counter.increment();
        }
        if (debug) {
            System.out.println("Result : " + Arrays.toString(result.toArray()));
        }
        return result;
    }

    @Override
    public List<E> sort(List<E> array, Counter counter, boolean debug) {
        if (array.size() <= 1) {
            return array;
        }
        List<E> left = new ArrayList<>(array.size() / 2);
        List<E> right = new ArrayList<>(array.size() / 2);
        for (int i = 0; i < array.size(); i++) {
            E element = array.get(i);
            if (i < array.size() / 2) {
                left.add(element);
            } else {
                right.add(element);
            }
            counter.increment();
        }
        if (debug) {
            System.out.println("Left   : " + Arrays.toString(left.toArray()));
            System.out.println("Right  : " + Arrays.toString(right.toArray()));
        }
        return merge(sort(left, counter, debug), sort(right, counter, debug), counter, debug);
    }
}
