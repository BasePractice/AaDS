package ru.mifi.practice.vol2;

public final class Factorial {
    private final int n;

    public Factorial(int n) {
        this.n = n;
    }

    private int recursiveFactorial(int n) {
        return n <= 1 ? 1 : n * recursiveFactorial(n - 1);
    }

    public int recursiveFactorial() {
        return recursiveFactorial(n);
    }

    public int iterationFactorial() {
        int fact = 1;
        for (int i = 1; i <= n; i++) {
            fact = fact * i;
        }
        return fact;
    }
}
