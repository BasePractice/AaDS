package ru.mifi.practice.vol2;

public final class Recursion2 {

    public static void main(String[] args) {
        new Recursion2().dec(3);
    }

    int dec(int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(i);
        }
        return 1;
    }
}
