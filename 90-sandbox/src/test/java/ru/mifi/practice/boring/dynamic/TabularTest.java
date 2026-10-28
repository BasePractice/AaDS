package ru.mifi.practice.boring.dynamic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Общая подпоследовательность по таблице")
final class TabularTest {

    @DisplayName("Находит общую подпоследовательность")
    @Test
    @Timeout(1)
    void findsTheCommonSubsequence() {
        assertThat("the common subsequence is wrong",
            new Tabular().longest("AGGTAB", "GXTXAYB"), is("GTAB"));
    }

    @DisplayName("Берёт разрозненные совпадения, а не подстроку")
    @Test
    @Timeout(1)
    void collectsTheScatteredMatches() {
        assertThat("the scattered matches are missed", new Tabular().longest("abcde", "ace"), is("ace"));
    }

    @DisplayName("У строк без общих букв подпоследовательность пуста")
    @Test
    @Timeout(1)
    void findsNothingInCommon() {
        assertThat("something common is found in disjoint strings",
            new Tabular().longest("abc", "xyz"), is(""));
    }

    @DisplayName("Пустая строка не даёт общего")
    @Test
    @Timeout(1)
    void findsNothingWithTheEmptyString() {
        assertThat("the empty string got something in common", new Tabular().longest("abc", ""), is(""));
    }
}
