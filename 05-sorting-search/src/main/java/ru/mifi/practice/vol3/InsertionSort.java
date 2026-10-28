package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Сортировка вставками: список делится на упорядоченное начало и неразобранный хвост, и каждый
 * очередной элемент опускается в начало до своего места. Худший случай — O(n²), почти
 * упорядоченный вход — O(n), поэтому вставками добирают хвосты быстрых сортировок.
 *
 * <p>Устойчива: элемент останавливается перед равным себе, а не за ним. Счётчик считает
 * сравнения с уже разобранной частью.
 */
public final class InsertionSort<E extends Comparable<E>> implements Sort<E> {

    @Override
    public List<E> sort(List<E> array, Counter counter, boolean debug) {
        List<E> sortable = new ArrayList<>(array);
        for (int i = 1; i < sortable.size(); i++) {
            E value = sortable.get(i);
            int place = i;
            while (place > 0 && greater(sortable.get(place - 1), value, counter)) {
                sortable.set(place, sortable.get(place - 1));
                --place;
            }
            sortable.set(place, value);
            if (debug) {
                System.out.println("Step " + i + ": " + Arrays.toString(sortable.toArray()));
            }
        }
        return sortable;
    }

    private boolean greater(E left, E right, Counter counter) {
        counter.increment();
        return left.compareTo(right) > 0;
    }
}
