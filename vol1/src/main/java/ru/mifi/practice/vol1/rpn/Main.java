package ru.mifi.practice.vol1.rpn;

public abstract class Main {
    public static void main(String[] args) {
        ReversePolishNotation rpn = new ReversePolishNotation.Default();
        assertEquals(rpn, "3 2 +", 5);
        assertEquals(rpn, "3 2 + 10 +", 15);
        assertEquals(rpn, "1 10 - abs", 9);
    }

    private static void assertEquals(ReversePolishNotation rpn, String text, Number expected) {
        Number evaluated = rpn.evaluate(text);
        if (expected.intValue() != evaluated.intValue()) {
            throw new AssertionError();
        }
    }
}
