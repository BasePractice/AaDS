package ru.mifi.practice.voln.trampoline;

import java.math.BigInteger;

public abstract class Main {

    private static Trampoline<BigInteger> factorial(BigInteger n, BigInteger acc) {
        if (n.equals(BigInteger.ONE)) {
            return Trampoline.done(acc);
        } else {
            return Trampoline.more(() -> factorial(n.subtract(BigInteger.ONE), acc.multiply(n)));
        }
    }

    public static BigInteger calculateFactorial(long n) {
        Trampoline<BigInteger> trampoline = factorial(BigInteger.valueOf(n), BigInteger.ONE);
        while (trampoline instanceof Trampoline.More<BigInteger> more) {
            trampoline = more.run();
        }
        return trampoline.get();
    }

    public static void main(String[] args) {
        System.out.println(calculateFactorial(5));
        System.out.println(calculateFactorial(10));
    }
}
