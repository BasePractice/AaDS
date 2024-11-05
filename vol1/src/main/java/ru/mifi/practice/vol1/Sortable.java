package ru.mifi.practice.vol1;

public interface Sortable<T extends Comparable<T>> {
    void sort(T[] data);
}
