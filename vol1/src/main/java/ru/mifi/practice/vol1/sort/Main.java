package ru.mifi.practice.vol1.sort;

import java.util.Arrays;

public abstract class Main {
    public static void main(String[] args) {
        Sortable<Integer> sortable = Sortable.Type.SELECTION.newSorting();
        Integer[] data = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        sortable.sort(data);
        System.out.println(Arrays.toString(data));
    }
}
