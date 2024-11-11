package ru.mifi.practice.vol2.combin;

import java.util.Optional;

public abstract class Main {

    private static void start(String x, String y, String z) {
        Optional<DigitSymbol.Equation<Number>> processed = new DigitSymbol.Simple().process(x, y, z);
        System.out.printf("%s + %s = %s: ", x, y, z);
        if (processed.isPresent()) {
            DigitSymbol.Equation<Number> equation = processed.get();
            System.out.printf("%s + %s = %s%n", equation.x(), equation.y(), equation.z());
        } else {
            System.out.printf("UNSOLVED%n");
        }
    }

    public static void main(String[] args) {
        start("x", "y", "z");
        start("win", "lose", "game");
        start("love", "hate", "feel");
        start("four", "seven", "eight");
        start("a", "b", "a");
        start("odin", "odin", "mnogo");
    }
}
