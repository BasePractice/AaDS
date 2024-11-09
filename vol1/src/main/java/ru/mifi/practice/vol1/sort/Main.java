package ru.mifi.practice.vol1.sort;

import java.util.Random;

public abstract class Main {
    private static final Random RANDOM = new Random();

    private static Integer[] generateSlice(int length) {
        Integer[] slice = new Integer[length];
        for (int i = 0; i < slice.length; i++) {
            slice[i] = RANDOM.nextInt(101);
        }
        return slice;
    }

    public static void main(String[] args) {
        Integer[] data = generateSlice(1000000);
        for (Sortable.Type type : Sortable.Type.values()) {
            Sortable<Integer> sortable = type.newSorting();
            Integer[] clone = data.clone();
            long millis = System.currentTimeMillis();
            sortable.sort(clone);
            System.out.println(type + ": " + (System.currentTimeMillis() - millis) + " ms.");
        }
    }
}
