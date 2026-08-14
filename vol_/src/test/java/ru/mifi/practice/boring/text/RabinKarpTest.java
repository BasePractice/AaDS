package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Поиск подстроки хешем")
final class RabinKarpTest {

    @DisplayName("Находит образец в тексте")
    @Test
    @Timeout(1)
    void findsThePattern() {
        assertThat("the pattern is missed",
            new RabinKarp().positions("абракадабра", List.of("кад")).get("кад"), contains(4));
    }

    @DisplayName("Находит перекрывающиеся вхождения")
    @Test
    @Timeout(1)
    void findsOverlappingOccurrences() {
        assertThat("the overlapping occurrences are missed",
            new RabinKarp().positions("aaaa", List.of("aa")).get("aa"), contains(0, 1, 2));
    }

    @DisplayName("Образца, которого нет, не находит")
    @Test
    @Timeout(1)
    void findsNothingWhenThePatternIsAbsent() {
        assertThat("an absent pattern is found",
            new RabinKarp().positions("абракадабра", List.of("щука")).get("щука"), is(List.of()));
    }

    @DisplayName("Образец длиннее текста не ищется")
    @Test
    @Timeout(1)
    void refusesThePatternLongerThanTheText() {
        assertThat("a pattern longer than the text is found",
            new RabinKarp().positions("абра", List.of("абракадабра")).get("абракадабра"), is(List.of()));
    }

    @DisplayName("Совпадение хешей проверяется посимвольно")
    @Test
    @Timeout(5)
    void checksTheMatchSymbolBySymbol() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            text.append('a');
        }
        assertThat("a hash collision passes for an occurrence",
            new RabinKarp().positions(text.toString(), List.of("ab")).get("ab"), is(List.of()));
    }
}
