package ru.mifi.practice.vol2.yasymb;

import java.util.Optional;

public abstract class Main {

    private static final YaSymbol YA = new SimpleGen1();

    private static void start(String x, String y, String z) {
        Optional<YaSymbol.Equation> processed = YA.process(x, y, z);
        if (processed.isPresent()) {
            System.out.printf("------ %7d -------%n", processed.get().metrics().getOperations());
        } else {
            System.out.printf("----------------------%n");
        }
        new YaSymbol.Equation(x, y, z, new YaSymbol.Metrics()).print();
        System.out.println();
        if (processed.isPresent()) {
            YaSymbol.Equation equation = processed.get();
            equation.print();
        } else {
            System.out.println("UNSOLVED");
        }
        System.out.printf("----------------------%n");
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
