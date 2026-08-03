package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;

/**
 * Пирамидальная сортировка из раздела 9 курса. Сначала массив за O(n) превращается в
 * максимум-кучу, затем n раз корень меняется с последним элементом, куча сжимается на единицу,
 * а новый корень погружается. Итог — O(n log n) в любом случае при O(1) дополнительной памяти.
 *
 * <p>Построение кучи стоит O(n), а не O(n log n): листьев много, но они дешёвые, а дорогой
 * корень ровно один. Неустойчива, и на практике проигрывает быстрой сортировке из-за прыжков
 * по индексам, которые плохо ложатся на кеш процессора.
 *
 * <p>Адресация нульиндексная, как в лекции: parent(i) = (i−1)/2, left(i) = 2i+1, right(i) = 2i+2.
 */
public final class HeapSort<E extends Comparable<E>> implements Sort<E> {

    @Override
    public List<E> sort(List<E> array, Counter counter, boolean debug) {
        List<E> sortable = new ArrayList<>(array);
        int size = sortable.size();
        for (int i = size / 2 - 1; i >= 0; i--) {
            sink(sortable, i, size, counter);
        }
        for (int last = size - 1; last > 0; last--) {
            swap(sortable, 0, last);
            sink(sortable, 0, last, counter);
        }
        return sortable;
    }

    private void sink(List<E> heap, int position, int size, Counter counter) {
        int current = position;
        while (true) {
            int largest = current;
            int left = current * 2 + 1;
            int right = current * 2 + 2;
            if (left < size && heap.get(left).compareTo(heap.get(largest)) > 0) {
                largest = left;
            }
            if (right < size && heap.get(right).compareTo(heap.get(largest)) > 0) {
                largest = right;
            }
            counter.increment();
            if (largest == current) {
                return;
            }
            swap(heap, current, largest);
            current = largest;
        }
    }

    private void swap(List<E> heap, int left, int right) {
        E value = heap.get(left);
        heap.set(left, heap.get(right));
        heap.set(right, value);
    }
}
