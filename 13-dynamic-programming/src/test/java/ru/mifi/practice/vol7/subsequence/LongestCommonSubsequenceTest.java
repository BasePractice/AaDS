package ru.mifi.practice.vol7.subsequence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка наибольшей общей подпоследовательности. */
@DisplayName("Наибольшая общая подпоследовательность")
final class LongestCommonSubsequenceTest {

    @DisplayName("Одинаковые строки совпадают целиком")
    @Test
    @Timeout(1)
    void matchesEqualStringsEntirely() {
        assertThat("equal strings dont match entirely",
            new LongestCommonSubsequence.Default()
                .longestCommonSubsequence("abcde", "abcde", Counter.create()), is(5));
    }

    @DisplayName("У строк без общих букв подпоследовательность пуста")
    @Test
    @Timeout(1)
    void findsNothingCommonInDisjointStrings() {
        assertThat("disjoint strings share a subsequence",
            new LongestCommonSubsequence.Default()
                .longestCommonSubsequence("abc", "xyz", Counter.create()), is(0));
    }

    @DisplayName("Подпоследовательность не обязана быть непрерывной")
    @Test
    @Timeout(1)
    void allowsGapsInTheSubsequence() {
        assertThat("the subsequence is forced to be contiguous",
            new LongestCommonSubsequence.Default()
                .longestCommonSubsequence("abcde", "ace", Counter.create()), is(3));
    }

    @DisplayName("Порядок букв важен")
    @Test
    @Timeout(1)
    void respectsTheOrderOfLetters() {
        assertThat("the order of letters is ignored",
            new LongestCommonSubsequence.Default()
                .longestCommonSubsequence("abc", "cba", Counter.create()), is(1));
    }

    @DisplayName("С пустой строкой общего нет")
    @Test
    @Timeout(1)
    void findsNothingCommonWithAnEmptyString() {
        assertThat("an empty string shares a subsequence",
            new LongestCommonSubsequence.Default()
                .longestCommonSubsequence("abc", "", Counter.create()), is(0));
    }
}
