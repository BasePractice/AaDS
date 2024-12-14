package ru.mifi.practice.vol7.fibonacci;

import ru.mifi.practice.vol7.Counter;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface Fibonacci {
    long fibonacci(int n, Counter counter);

    final class Dynamited implements Fibonacci {
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

    final class Memorized implements Fibonacci {
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

    final class Recursion implements Fibonacci {
        @Override
        public long fibonacci(int n, Counter counter) {
            counter.increment();
            if (n == 0 || n == 1) {
                return 1;
            }
            return fibonacci(n - 1, counter) + fibonacci(n - 2, counter);
        }
    }
}
