package ru.mifi.practice.boring.dynamic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Общая подпоследовательность по Хиршбергу")
final class HirschbergTest {

    @DisplayName("Находит общую подпоследовательность")
    @Test
    @Timeout(1)
    void findsTheCommonSubsequence() {
        assertThat("the common subsequence is wrong",
            new Hirschberg().longest("AGGTAB", "GXTXAYB"), is("GTAB"));
    }

    @DisplayName("У строк без общих букв подпоследовательность пуста")
    @Test
    @Timeout(1)
    void findsNothingInCommon() {
        assertThat("something common is found in disjoint strings",
            new Hirschberg().longest("abc", "xyz"), is(""));
    }

    @DisplayName("Одинаковые строки совпадают целиком")
    @Test
    @Timeout(1)
    void takesTheWholeEqualString() {
        assertThat("equal strings are cut short", new Hirschberg().longest("привет", "привет"), is("привет"));
    }

    @DisplayName("Пустая строка не даёт общего")
    @Test
    @Timeout(1)
    void findsNothingWithTheEmptyString() {
        assertThat("the empty string got something in common",
            new Hirschberg().longest("", "abc"), is(""));
    }

    @DisplayName("Находит то же по длине, что табличный способ")
    @Test
    @Timeout(10)
    void agreesWithTheTabularWay() {
        Random random = new Random(20260815L);
        Subsequence linear = new Hirschberg();
        Subsequence tabular = new Tabular();
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            String left = word(random, random.nextInt(20));
            String right = word(random, random.nextInt(20));
            if (linear.longest(left, right).length() != tabular.longest(left, right).length()) {
                ++mismatches;
            }
        }
        assertThat("the linear memory way disagrees with the tabular one", mismatches, is(0));
    }

    @DisplayName("Ответ действительно является подпоследовательностью обеих строк")
    @Test
    @Timeout(10)
    void answersWithASubsequenceOfBoth() {
        Random random = new Random(20260815L);
        int violations = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            String left = word(random, random.nextInt(20));
            String right = word(random, random.nextInt(20));
            String common = new Hirschberg().longest(left, right);
            if (!subsequence(common, left) || !subsequence(common, right)) {
                ++violations;
            }
        }
        assertThat("the answer is not a subsequence of both strings", violations, is(0));
    }

    private static boolean subsequence(String part, String whole) {
        int at = 0;
        for (char symbol : whole.toCharArray()) {
            if (at < part.length() && part.charAt(at) == symbol) {
                ++at;
            }
        }
        return at == part.length();
    }

    private static String word(Random random, int length) {
        StringBuilder word = new StringBuilder();
        for (int at = 0; at < length; at++) {
            word.append((char) ('a' + random.nextInt(4)));
        }
        return word.toString();
    }
}
