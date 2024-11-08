package ru.mifi.practice.vol1.cbs;

public abstract class Main {
    public static void main(String[] args) {
        CorrectBriceSequence cbs = new CorrectBriceSequence.Default();
        assertTrue(cbs, "(.)(.)");
        assertTrue(cbs, "()");
        assertTrue(cbs, "()(())(()())");
        assertTrue(cbs, "({})((){})");
    }

    private static void assertTrue(CorrectBriceSequence cbs, String input) {
        if (!cbs.isCorrect(input)) {
            throw new AssertionError(input);
        }
    }
}
