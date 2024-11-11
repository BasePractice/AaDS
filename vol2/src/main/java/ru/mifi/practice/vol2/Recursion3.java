package ru.mifi.practice.vol2;

public final class Recursion3 {

    private int step = 0;

    public static void main(String[] args) {
        new Recursion3().dec(10, 0);
    }

    private void printStep(int pad, int n, String message) {
        System.out.printf("%3d", step);
        for (int i = 0; i < pad; i++) {
            System.out.print(" ");
        }
        System.out.printf("%s: %d%n", message, n);
        ++step;
    }

    int dec(int n, int pad) {
        if (n <= 0) {
            return 0;
        }
        printStep(pad, n, "[");
        dec(n - 1, pad + 1);
        printStep(pad, n, "]");
        return 1;
    }
}
