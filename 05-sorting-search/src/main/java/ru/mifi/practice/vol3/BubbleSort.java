package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Пузырьковая сортировка: соседи сравниваются попарно, больший всплывает к концу списка, и после
 * каждого прохода хвост длиной в проход уже упорядочен. Худший случай — O(n²), лучший — O(n),
 * потому что проход без обменов означает, что список отсортирован, и остальные проходы не нужны.
 *
 * <p>Счётчик считает сравнения соседей — на упорядоченном входе их ровно n−1, на развёрнутом
 * n(n−1)/2. Разница с O(n log n) видна на одних и тех же данных в MainSort.
 */
public final class BubbleSort<E extends Comparable<E>> implements Sort<E> {

    @Override
    public List<E> sort(List<E> array, Counter counter, boolean debug) {
        List<E> sortable = new ArrayList<>(array);
        for (int pass = 0; pass < sortable.size(); pass++) {
            boolean swapped = false;
            for (int i = 1; i < sortable.size() - pass; i++) {
                counter.increment();
                if (sortable.get(i - 1).compareTo(sortable.get(i)) > 0) {
                    swap(sortable, i - 1, i);
                    swapped = true;
                }
            }
            if (debug) {
                System.out.println("Pass " + pass + ": " + Arrays.toString(sortable.toArray()));
            }
            if (!swapped) {
                return sortable;
            }
        }
        return sortable;
    }

    private void swap(List<E> array, int left, int right) {
        E value = array.get(left);
        array.set(left, array.get(right));
        array.set(right, value);
    }
}
