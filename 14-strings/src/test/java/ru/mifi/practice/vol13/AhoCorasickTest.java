package ru.mifi.practice.vol13;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Поиск многих образцов по Ахо — Корасику")
final class AhoCorasickTest {

    @DisplayName("Находит образец, начинающийся внутри другого")
    @Test
    @Timeout(1)
    void findsThePatternInsideAnother() {
        assertThat("the pattern hidden inside another one is missed",
            new AhoCorasick(List.of("he", "she", "his", "hers")).all("ushers", Counter.create()).get("he"),
            contains(2));
    }

    @DisplayName("Находит все образцы за один проход")
    @Test
    @Timeout(1)
    void findsEveryPatternInASingleWalk() {
        Map<String, List<Integer>> found = new AhoCorasick(List.of("he", "she", "his", "hers"))
            .all("ushers", Counter.create());
        assertThat("the found patterns dont match the expected ones",
            found, is(Map.of("he", List.of(2), "she", List.of(1), "his", List.of(), "hers", List.of(2))));
    }

    @DisplayName("Находит перекрывающиеся вхождения одного образца")
    @Test
    @Timeout(1)
    void findsOverlappingOccurrences() {
        assertThat("the overlapping occurrences are missed",
            new AhoCorasick(List.of("aa")).all("aaaa", Counter.create()).get("aa"),
            contains(0, 1, 2));
    }

    @DisplayName("Отдаёт пустой список для образца, которого в тексте нет")
    @Test
    @Timeout(1)
    void reportsTheMissingPatternAsEmpty() {
        assertThat("the missing pattern is reported as found",
            new AhoCorasick(List.of("xyz")).all("abcabc", Counter.create()).get("xyz"),
            is(List.of()));
    }

    @DisplayName("Читает каждый символ текста ровно раз")
    @Test
    @Timeout(1)
    void readsEverySymbolOnce() {
        Counter counter = Counter.create();
        new AhoCorasick(List.of("he", "she", "his", "hers")).all("ushers", counter);
        assertThat("the text is walked more than once", counter.count(), is(6));
    }

    @DisplayName("Отказывается искать пустой образец")
    @Test
    @Timeout(1)
    void refusesTheEmptyPattern() {
        assertThat("the empty pattern is silently searched for",
            assertThrows(IllegalArgumentException.class, () -> new AhoCorasick(List.of("he", ""))).getMessage(),
            is("Пустой образец совпадает с любой позицией текста"));
    }

    @DisplayName("Находит то же, что Кнут — Моррис — Пратт для каждого образца")
    @Test
    @Timeout(5)
    void agreesWithKnuthMorrisPratt() {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            String text = word(random, 40);
            List<String> patterns = List.of(word(random, 2), word(random, 3), word(random, 1));
            Map<String, List<Integer>> found = new AhoCorasick(patterns).all(text, Counter.create());
            for (String pattern : patterns) {
                if (!found.get(pattern).equals(new Substring.Knuth().all(text, pattern, Counter.create()))) {
                    ++mismatches;
                }
            }
        }
        assertThat("the automaton disagrees with the knuth morris pratt search", mismatches, is(0));
    }

    private static String word(Random random, int length) {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(3)));
        }
        return word.toString();
    }
}
