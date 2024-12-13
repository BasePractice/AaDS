package ru.mifi.practice.vol7.fibonacci;

import ru.mifi.practice.vol7.Counter;

public final class FibonacciDynamicus implements Fibonacci {
    @Override
    public long fibonacci(int n, Counter counter) {
        long value1 = 1;
        long value2 = 1;
        long value3 = 1;
        for (int i = 2; i <= n; i++) {
            value3 = value1 + value2;
            value1 = value2;
            value2 = value3;
            counter.increment();
        }
        return value3;
    }
}
