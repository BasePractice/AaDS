package ru.mifi.practice.vol7.fibonacci;

import ru.mifi.practice.vol7.Counter;

import java.util.HashMap;
import java.util.Map;

public final class FibonacciMemorized implements Fibonacci {
    private static long fibonacci(int n, Map<Integer, Long> memo, Counter counter) {
        counter.increment();
        if (n == 0 || n == 1) {
            return 1;
        } else if (memo.containsKey(n)) {
            return memo.get(n);
        }
        long left = fibonacci(n - 1, memo, counter);
        long right = fibonacci(n - 2, memo, counter);
        memo.put(n, left + right);
        return left + right;
    }

    @Override
    public long fibonacci(int n, Counter counter) {
        return fibonacci(n, new HashMap<>(), counter);
    }
}
