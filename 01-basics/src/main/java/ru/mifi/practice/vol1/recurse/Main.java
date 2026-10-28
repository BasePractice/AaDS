package ru.mifi.practice.vol1.recurse;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Main {
    private static final AtomicLong CALLING = new AtomicLong(0);

    private static long fibonacci(int n) {
        if (n < 2) {
            return n;
        }
        CALLING.incrementAndGet();
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void main(String[] args) {
        System.out.println("FIB : " + fibonacci(50));
        System.out.println("CALL: " + CALLING.get());
    }
}
