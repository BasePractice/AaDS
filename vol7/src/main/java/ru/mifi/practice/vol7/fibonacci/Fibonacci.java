package ru.mifi.practice.vol7.fibonacci;

import ru.mifi.practice.vol7.Counter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface Fibonacci {
    BigInteger fibonacci(int n, Counter counter);

    final class Dynamited implements Fibonacci {
        @Override
        public BigInteger fibonacci(int n, Counter counter) {
            BigInteger value1 = BigInteger.ONE;
            BigInteger value2 = BigInteger.ONE;
            BigInteger value3 = BigInteger.ONE;
            for (int i = 2; i <= n; i++) {
                value3 = value1.add(value2);
                value1 = value2;
                value2 = value3;
                counter.increment();
            }
            return value3;
        }
    }

    final class Memorized implements Fibonacci {
        private static BigInteger fibonacci(int n, Map<Integer, BigInteger> memo, Counter counter) {
            counter.increment();
            if (n == 0 || n == 1) {
                return BigInteger.ONE;
            } else if (memo.containsKey(n)) {
                return memo.get(n);
            }
            BigInteger left = fibonacci(n - 1, memo, counter);
            BigInteger right = fibonacci(n - 2, memo, counter);
            BigInteger added = left.add(right);
            memo.put(n, added);
            return added;
        }

        @Override
        public BigInteger fibonacci(int n, Counter counter) {
            return fibonacci(n, new HashMap<>(), counter);
        }
    }

    final class Recursion implements Fibonacci {
        @Override
        public BigInteger fibonacci(int n, Counter counter) {
            counter.increment();
            if (n == 0 || n == 1) {
                return BigInteger.ONE;
            }
            return fibonacci(n - 1, counter).add(fibonacci(n - 2, counter));
        }
    }
}
