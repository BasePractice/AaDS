package ru.mifi.practice.vol1.o;

@SuppressWarnings("PMD.UnusedPrivateMethod")
public abstract class Main {

    private static int sum(int a, int b) {
        return a + b;
    }

    private static int sum(int[] a) {
        int n = 0;
        for (int j : a) {
            n += j;
        }
        return n;
    }


    public static void main(String[] args) {
        assert sum(1, 1) == 2;
        assert sum(new int[]{1, 1}) == 2;
    }
}
