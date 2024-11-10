package ru.mifi.practice.vol2;

public abstract class MathTests {
    public static void main(String[] args) {
        MathCalculator calculator = new MathCalculator.Simple();
        assertEquals(calculator, "2", 2);
        assertEquals(calculator, "2 * 4", 8);
        assertEquals(calculator, "2 + 4", 6);
        assertEquals(calculator, "2 + 4 + 15", 21);
        assertEquals(calculator, "2 * 4 + 15", 23);
        assertEquals(calculator, "2 + 4 * 15", 62);
        assertEquals(calculator, "( 2 + 4 ) * 15", 90);
        assertEquals(calculator, "2 + ( 4 * 15 )", 62);
        assertEquals(calculator, "2 * ( 4 + 15 )", 38);
        assertEquals(calculator, "( 4 * ( 3 + 7 ) ) * 3 ", 120);
    }

    private static void assertEquals(MathCalculator calculator, String expression, double actual) {
        Number evaluated = calculator.evaluate(expression);
        if (evaluated.doubleValue() != actual) {
            throw new ArithmeticException(expression + ": " + evaluated + " != " + actual);
        }
    }
}
