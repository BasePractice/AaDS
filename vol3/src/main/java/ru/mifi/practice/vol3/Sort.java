package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import java.util.List;

/** Стратегия сортировки списка сравнимых элементов с подсчётом сравнений. */
@FunctionalInterface
public interface Sort<E extends Comparable<E>> {
    List<E> sort(List<E> array, Counter counter, boolean debug);
}
