package ru.mifi.practice.vol7;

import ru.mifi.practice.vol7.fibonacci.Fibonacci;
import ru.mifi.practice.vol7.fibonacci.FibonacciDynamicus;
import ru.mifi.practice.vol7.fibonacci.FibonacciMemorized;
import ru.mifi.practice.vol7.fibonacci.FibonacciRecursion;

public abstract class Main {
    public static void main(String[] args) {
        fibonacci("Recursion", new FibonacciRecursion());
        fibonacci("Memorized", new FibonacciMemorized());
        fibonacci("Dynamicus", new FibonacciDynamicus());
    }

    private static void fibonacci(String name, Fibonacci fibonacci) {
        Timed<Long> timed = new Timed.Elapsed<>();
        Counter counter = new Counter.Default();
        String prefix = String.format("Fib.%s.", name);
        long v = timed.timed(prefix + "Time", 0L, (init) -> fibonacci.fibonacci(40, counter));
        System.out.println(prefix + "Cnt : " + counter);
        System.out.println(prefix + "Val : " + v);
        counter.reset();
    }
}
