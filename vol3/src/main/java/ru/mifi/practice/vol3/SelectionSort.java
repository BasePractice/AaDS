package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Сортировка выбором: на каждом шаге в неразобранном хвосте ищется минимум и меняется местами с
 * первым элементом хвоста. Сравнений всегда ровно n(n−1)/2 — упорядоченный вход стоит столько же,
 * сколько развёрнутый, зато обменов не больше n−1, и этим она полезна, когда обмен дорог.
 *
 * <p>Неустойчива: дальний минимум перепрыгивает через равные ключи в начале хвоста.
 */
public final class SelectionSort<E extends Comparable<E>> implements Sort<E> {

    @Override
    public List<E> sort(List<E> array, Counter counter, boolean debug) {
        List<E> sortable = new ArrayList<>(array);
        for (int i = 0; i < sortable.size() - 1; i++) {
            int minimum = i;
            for (int j = i + 1; j < sortable.size(); j++) {
                counter.increment();
                if (sortable.get(minimum).compareTo(sortable.get(j)) > 0) {
                    minimum = j;
                }
            }
            swap(sortable, i, minimum);
            if (debug) {
                System.out.println("Step " + i + ": " + Arrays.toString(sortable.toArray()));
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
