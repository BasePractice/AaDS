package ru.mifi.practice.vol2.yasymb.stage2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Решение ребуса сложения")
final class Stage2Test {

    @DisplayName("Разрешимость ребуса совпадает с ожидаемой")
    @ParameterizedTest
    @Timeout(5)
    @CsvSource({
        "a, a, b, true",
        "a, b, c, true",
        "ab, cd, ef, true",
        "four, seven, eight, true",
        "send, more, money, true",
        "aa, a, a, false",
        "a, a, a, false",
        "ab, ab, a, false",
        "a, a, aa, false",
        "ba, ba, a, false",
    })
    void solvesExpectedPuzzles(String x, String y, String z, boolean expected) {
        assertThat("solver disagrees with the expected solvability",
            Stage2.start(x, y, z, false), is(expected));
    }
}
