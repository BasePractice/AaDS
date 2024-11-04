package ru.mifi.practice.vol1;

public final class SelectionSorting<T extends Comparable<T>> implements Sortable<T> {
    public void sort(T[] data) {
        int minimum;
        for (int i = 0; i < data.length - 1; i++) {
            minimum = i;
            for (int j = i + 1; j < data.length; j++) {
                if (data[minimum].compareTo(data[j]) > 0) {
                    minimum = j;
                }
            }
            T temp = data[minimum];
            data[minimum] = data[i];
            data[i] = temp;
        }
    }
}
