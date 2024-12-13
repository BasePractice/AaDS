package ru.mifi.practice.vol7.fibonacci;

import ru.mifi.practice.vol7.Counter;

public final class FibonacciRecursion implements Fibonacci {
    @Override
    public long fibonacci(int n, Counter counter) {
        counter.increment();
        if (n == 0 || n == 1) {
            return 1;
        }
        return fibonacci(n - 1, counter) + fibonacci(n - 2, counter);
    }
}
