package ru.mifi.practice.vol13;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.commons.Counter;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Поиск подстроки")
final class SubstringTest {

    static Stream<Substring> implementations() {
        return Stream.of(new Substring.Knuth(), new Substring.RabinKarp());
    }

    @DisplayName("Находит образец в начале")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void findsThePatternAtTheStart(Substring search) {
        assertThat("a pattern at the start dont get found",
            search.first("абракадабра", "абра", Counter.create()).orElseThrow(), is(0));
    }

    @DisplayName("Находит образец в конце")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void findsThePatternAtTheEnd(Substring search) {
        assertThat("a pattern ending at the last character dont get found",
            search.first("абракадабра", "дабра", Counter.create()).orElseThrow(), is(6));
    }

    @DisplayName("Находит образец, равный всему тексту")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void findsThePatternEqualToTheText(Substring search) {
        assertThat("a pattern equal to the text dont get found",
            search.first("абра", "абра", Counter.create()).orElseThrow(), is(0));
    }

    @DisplayName("Отсутствующий образец не находится")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void reportsAMissingPattern(Substring search) {
        assertThat("a missing pattern is reported as found",
            search.first("абракадабра", "щука", Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Образец длиннее текста не находится")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void reportsAPatternLongerThanTheText(Substring search) {
        assertThat("a pattern longer than the text is reported as found",
            search.first("абра", "абракадабра", Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Находит все вхождения")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void findsEveryOccurrence(Substring search) {
        assertThat("not every occurrence gets found",
            search.all("абракадабра", "абра", Counter.create()), contains(0, 7));
    }

    @DisplayName("Находит перекрывающиеся вхождения")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void findsOverlappingOccurrences(Substring search) {
        assertThat("overlapping occurrences are skipped",
            search.all("аааа", "аа", Counter.create()), contains(0, 1, 2));
    }

    @DisplayName("Пустой образец не ищется")
    @ParameterizedTest
    @Timeout(1)
    @MethodSource("implementations")
    void refusesAnEmptyPattern(Substring search) {
        assertThat("an empty pattern is reported as found",
            search.first("абра", "", Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Префикс-функция считает длины границ")
    @Test
    @Timeout(1)
    void computesBorderLengths() {
        assertThat("prefix function dont compute the border lengths",
            Substring.Knuth.prefixes("абабв", Counter.create())[3], is(2));
    }

    @DisplayName("Префикс-функция строки без повторов нулевая")
    @Test
    @Timeout(1)
    void leavesTheBordersEmptyWithoutRepetitions() {
        assertThat("prefix function finds a border where there is none",
            Substring.Knuth.prefixes("абв", Counter.create())[2], is(0));
    }

    @DisplayName("КМП не возвращается по тексту")
    @Test
    @Timeout(1)
    void neverGoesBackOverTheText() {
        Counter counter = Counter.create();
        new Substring.Knuth().first("а".repeat(1000) + "б", "а".repeat(10) + "б", counter);
        assertThat("the search degrades to a quadratic scan", counter.count() < 4000, is(true));
    }
}
