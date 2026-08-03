package ru.mifi.practice.vol4.hash;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Поиск подстроки")
final class SearchTest {

    @DisplayName("Находит подстроку в начале")
    @Test
    void findsSubstringAtTheStart() {
        assertThat("substring at the start dont get found",
            new Search.SimpleSearch().search("abcabc", "abc", new Counter.Default()).orElseThrow().index(), is(0));
    }

    @DisplayName("Находит подстроку в конце")
    @Test
    void findsSubstringAtTheEnd() {
        assertThat("substring ending at the last character dont get found",
            new Search.SimpleSearch().search("abc", "bc", new Counter.Default()).orElseThrow().index(), is(1));
    }

    @DisplayName("Находит подстроку после частичного совпадения")
    @Test
    void findsSubstringAfterPartialMatch() {
        assertThat("a partial match makes the search skip the real one",
            new Search.SimpleSearch().search("aab", "ab", new Counter.Default()).orElseThrow().index(), is(1));
    }

    @DisplayName("Находит подстроку, равную всему тексту")
    @Test
    void findsSubstringEqualToTheWholeText() {
        assertThat("substring equal to the text dont get found",
            new Search.SimpleSearch().search("abc", "abc", new Counter.Default()).orElseThrow().index(), is(0));
    }

    @DisplayName("Отсутствующая подстрока не находится")
    @Test
    void reportsMissingSubstring() {
        assertThat("a missing substring is reported as found",
            new Search.SimpleSearch().search("abc", "xy", new Counter.Default()).isPresent(), is(false));
    }

    /**
     * Задание к разделу «Хеширование»: в PolynomialSearchCached есть ошибка, помеченная в JavaDoc.
     * Слева хеш приведён по модулю, а справа произведение subHash на степень основания — нет,
     * поэтому сравнение переполняется и почти всегда даёт ложь. Снимите @Disabled и почините.
     */
    @Disabled("Учебное задание: ошибка в PolynomialSearchCached")
    @DisplayName("Поиск по хешам находит подстроку")
    @Test
    void polynomialSearchFindsSubstring() {
        assertThat("hash based search compares a reduced hash against an unreduced product",
            new Search.PolynomialSearchCached().search("abcabc", "cab", new Counter.Default()).orElseThrow().index(),
            is(2));
    }
}
