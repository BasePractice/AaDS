package ru.mifi.practice.vol1;

import java.util.Collection;

public interface Sortable<T extends Comparable<T>> {
    void sort(T[] data);
}
