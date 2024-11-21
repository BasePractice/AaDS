package ru.mifi.practice.vol3;

import java.util.List;

@FunctionalInterface
public interface Sort<E extends Comparable<E>> {
    List<E> sort(List<E> array, Counter counter, boolean debug);
}
