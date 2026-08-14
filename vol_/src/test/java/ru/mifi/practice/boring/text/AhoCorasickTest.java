package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Поиск многих образцов автоматом")
final class AhoCorasickTest {

    @DisplayName("Находит образец, спрятанный внутри другого")
    @Test
    @Timeout(1)
    void findsThePatternInsideAnother() {
        assertThat("the pattern hidden inside another one is missed",
            new AhoCorasick().positions("ushers", List.of("he", "she", "his", "hers")).get("he"),
            contains(2));
    }

    @DisplayName("Находит все образцы разом")
    @Test
    @Timeout(1)
    void findsEveryPatternAtOnce() {
        assertThat("the found patterns dont match the expected ones",
            new AhoCorasick().positions("ushers", List.of("he", "she", "his", "hers")),
            is(Map.of("he", List.of(2), "she", List.of(1), "his", List.of(), "hers", List.of(2))));
    }

    @DisplayName("Находит перекрывающиеся вхождения одного образца")
    @Test
    @Timeout(1)
    void findsOverlappingOccurrences() {
        assertThat("the overlapping occurrences are missed",
            new AhoCorasick().positions("aaaa", List.of("aa")).get("aa"), contains(0, 1, 2));
    }

    @DisplayName("Сползает по суффиксной ссылке, а не возвращается по тексту")
    @Test
    @Timeout(1)
    void slidesAlongTheSuffixLink() {
        assertThat("the automaton loses an occurrence after a mismatch",
            new AhoCorasick().positions("aabab", List.of("abab")).get("abab"), contains(1));
    }

    @DisplayName("Пустой образец отвергается сразу")
    @Test
    @Timeout(1)
    void refusesTheEmptyPattern() {
        assertThat("the empty pattern is silently searched for",
            assertThrows(IllegalArgumentException.class,
                () -> new AhoCorasick().positions("текст", List.of(""))).getMessage(),
            is("Пустой образец совпадает с любой позицией текста"));
    }
}
