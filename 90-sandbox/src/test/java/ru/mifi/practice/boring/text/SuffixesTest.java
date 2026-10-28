package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Суффиксный массив и наибольшие общие префиксы")
final class SuffixesTest {

    @DisplayName("Упорядочивает суффиксы лексикографически")
    @Test
    @Timeout(1)
    void ordersTheSuffixes() {
        assertThat("the suffixes are out of lexicographic order",
            new Suffixes("banana").order(), is(new int[]{5, 3, 1, 0, 4, 2}));
    }

    @DisplayName("Считает общие префиксы соседних суффиксов")
    @Test
    @Timeout(1)
    void countsThePrefixesOfNeighbours() {
        assertThat("the common prefixes of the neighbours are wrong",
            new Suffixes("banana").prefixes(), is(new int[]{1, 3, 0, 0, 2, 0}));
    }

    @DisplayName("У строки без повторов общих префиксов нет")
    @Test
    @Timeout(1)
    void findsNoCommonPrefixesWithoutRepeats() {
        assertThat("a text without repeats got a common prefix",
            new Suffixes("abcd").prefixes(), is(new int[]{0, 0, 0, 0}));
    }

    @DisplayName("Строка из одинаковых букв даёт убывающие префиксы")
    @Test
    @Timeout(1)
    void countsThePrefixesOfARepeatedLetter() {
        assertThat("the prefixes of a repeated letter are wrong",
            new Suffixes("aaaa").prefixes(), is(new int[]{1, 2, 3, 0}));
    }

    @DisplayName("Считает то же, что прямое сравнение суффиксов")
    @Test
    @Timeout(10)
    void agreesWithThePlainComparison() {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 50; attempt++) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < 1 + random.nextInt(30); i++) {
                text.append((char) ('a' + random.nextInt(3)));
            }
            Suffixes suffixes = new Suffixes(text.toString());
            int[] order = suffixes.order();
            int[] prefixes = suffixes.prefixes();
            for (int place = 0; place + 1 < order.length; place++) {
                if (prefixes[place] != common(text.toString(), order[place], order[place + 1])) {
                    ++mismatches;
                }
            }
        }
        assertThat("the kasai prefixes disagree with the plain comparison", mismatches, is(0));
    }

    private static int common(String text, int left, int right) {
        int matched = 0;
        while (left + matched < text.length() && right + matched < text.length()
            && text.charAt(left + matched) == text.charAt(right + matched)) {
            ++matched;
        }
        return matched;
    }
}
