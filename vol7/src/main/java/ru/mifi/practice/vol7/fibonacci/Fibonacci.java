package ru.mifi.practice.vol7.fibonacci;

import ru.mifi.practice.vol7.Counter;

@FunctionalInterface
public interface Fibonacci {
    long fibonacci(int n, Counter counter);
}
