package ru.mifi.practice.vol1.bigo;

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

    private static int bitCount(int number) {
        int n = 0;
        while (number > 0) {
            n += number % 2;
            number /= 2;
        }
        return n;
    }

    private static long pow2(int n) {
        long p = 1;
        for (int i = 0; i < n; i++) {
            p = p * 2;
        }
        return p;
    }

    private static long pow20(int n) {
        return (long) 1 << n;
    }

    public static void main(String[] args) {
        assert sum(1, 1) == 2;
        assert sum(2, 2) == 4;
        assert sum(new int[]{1, 1}) == 2;
        assert bitCount(1) == 1;
        assert bitCount(2) == 2;
        assert bitCount(3) == 2;
        assert pow2(1) == 2;
        assert pow2(2) == 4;
        assert pow20(2) == 4;
        assert pow20(1) == 2;
    }
}
