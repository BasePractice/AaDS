package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка обоих способов поиска между собой и с прямым перебором на случайных текстах.
 *
 * <p>Алфавит из трёх букв: так вхождения и ложные совпадения хешей попадаются часто, а не раз
 * в тысячу прогонов.
 */
@DisplayName("Поиск образцов на случайных текстах")
final class SearchRandomTest {

    @DisplayName("Поиск хешем находит то же, что прямой перебор")
    @Test
    @Timeout(10)
    void rabinKarpAgreesWithThePlainWalk() {
        assertThat("the hash search disagrees with the plain walk",
            disagreements(new RabinKarp()), is(0));
    }

    @DisplayName("Автомат находит то же, что прямой перебор")
    @Test
    @Timeout(10)
    void ahoCorasickAgreesWithThePlainWalk() {
        assertThat("the automaton disagrees with the plain walk",
            disagreements(new AhoCorasick()), is(0));
    }

    private static int disagreements(Search search) {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            String text = word(random, 60);
            List<String> patterns = List.of(word(random, 1), word(random, 2), word(random, 3));
            for (String pattern : patterns) {
                if (!search.positions(text, patterns).get(pattern).equals(walked(text, pattern))) {
                    ++mismatches;
                }
            }
        }
        return mismatches;
    }

    private static List<Integer> walked(String text, String pattern) {
        List<Integer> found = new ArrayList<>();
        for (int at = 0; at + pattern.length() <= text.length(); at++) {
            if (text.startsWith(pattern, at)) {
                found.add(at);
            }
        }
        return found;
    }

    private static String word(Random random, int length) {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(3)));
        }
        return word.toString();
    }
}
