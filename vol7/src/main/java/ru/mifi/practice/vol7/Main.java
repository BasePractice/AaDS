package ru.mifi.practice.vol7;

import ru.mifi.practice.vol7.backpack.Backpack;
import ru.mifi.practice.vol7.distance.Distance;
import ru.mifi.practice.vol7.distance.Levenshtein;
import ru.mifi.practice.vol7.fibonacci.Fibonacci;
import ru.mifi.practice.vol7.subsequence.LongestCommonSubsequence;
import ru.mifi.practice.vol7.wildcard.Match;

import java.util.List;

public abstract class Main {
    public static void main(String[] args) {
        fibonacci("Recursion", new Fibonacci.Recursion());
        fibonacci("Memorized", new Fibonacci.Memorized());
        fibonacci("Dynamicus", new Fibonacci.Dynamited());
        Backpack backpack = new Backpack.Classic(10);
        List<Backpack.Item> items = List.of(
            new Backpack.Item("Кирпич", 1, 1),
            new Backpack.Item("Самогон", 1, 3),
            new Backpack.Item("Лопата", 1, 3),
            new Backpack.Item("Молот", 10, 1),
            new Backpack.Item("Еда", 3, 3),
            new Backpack.Item("Пиво", 6, 4)
        );
        List<Backpack.Item> putting = backpack.putting(items);
        System.out.println("         Backpack : " + putting);
        distance("Lev.Recur", new Levenshtein.LevenshteinRecursion(), "boobs", "bomb");
        distance("Lev.Dynam", new Levenshtein.VagnerFisherDynamited(), "boobs", "bomb");
        lcs("Sub.Commo", new LongestCommonSubsequence.Default(), "mouse", "house");
        match("Dyn.Regex", new Match.DefaultMatch(), "non? po?", "none pop");
        match("Dyn.Aggrv", new Match.AbbreviationMatch(), "Algorithm and Data Structure", "ADS");
    }

    private static void fibonacci(String name, Fibonacci fibonacci) {
        Timed<Long, Void> timed = new Timed.Elapsed<>();
        Counter counter = new Counter.Default();
        String prefix = String.format("Fib.%s.", name);
        long v = timed.timed(prefix + "Time", null, (nothing) -> fibonacci.fibonacci(40, counter));
        System.out.println(prefix + "Cnt : " + counter);
        System.out.println(prefix + "Val : " + v);
        counter.reset();
    }

    private static void distance(String name, Distance distance, String word1, String word2) {
        Counter counter = new Counter.Default();
        String prefix = String.format("Dis.%s.", name);
        int d = distance.distance(word1, word2, counter);
        System.out.println(prefix + "Val : " + d);
        System.out.println(prefix + "Cnt : " + counter);
    }

    private static void lcs(String name, LongestCommonSubsequence subsequence, String word1, String word2) {
        Counter counter = new Counter.Default();
        String prefix = String.format("LCS.%s.", name);
        int d = subsequence.longestCommonSubsequence(word1, word2, counter);
        System.out.println(prefix + "Val : " + d);
        System.out.println(prefix + "Cnt : " + counter);
    }

    private static void match(String name, Match match, String pattern, String text) {
        Counter counter = new Counter.Default();
        String prefix = String.format("Mch.%s.", name);
        var m = match.isMatch(pattern, text, counter);
        System.out.println(prefix + "Val : " + m);
        System.out.println(prefix + "Cnt : " + counter);
    }
}
