package ru.mifi.practice.vol2.recurs;

public final class Recursion {

    public static void main(String[] args) {
        new Recursion().dec(3);
    }

    void dec(int n) {
        System.out.println(n);
        if (n <= 1) {
            return;
        }
        dec(n - 1);
    }
}
