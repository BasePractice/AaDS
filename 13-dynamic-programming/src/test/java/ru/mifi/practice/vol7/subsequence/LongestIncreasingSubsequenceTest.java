package ru.mifi.practice.vol7.subsequence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Наибольшая возрастающая подпоследовательность")
final class LongestIncreasingSubsequenceTest {

    @DisplayName("Пустая последовательность не содержит возрастающей")
    @Test
    @Timeout(1)
    void findsNothingInTheEmptySequence() {
        assertThat("the empty sequence holds an increasing subsequence",
            new LongestIncreasingSubsequence.Dynamited().longest(List.of(), Counter.create()), is(0));
    }

    @DisplayName("Возрастающая последовательность целиком возрастает")
    @Test
    @Timeout(1)
    void takesTheWholeIncreasingSequence() {
        assertThat("the increasing sequence is cut short",
            new LongestIncreasingSubsequence.Dynamited().longest(List.of(1, 2, 3, 4), Counter.create()), is(4));
    }

    @DisplayName("Равные элементы не считаются возрастанием")
    @Test
    @Timeout(1)
    void refusesEqualElementsAsIncreasing() {
        assertThat("equal elements pass for an increasing subsequence",
            new LongestIncreasingSubsequence.Dynamited().longest(List.of(7, 7, 7), Counter.create()), is(1));
    }

    @DisplayName("Собирает подпоследовательность из разрозненных элементов")
    @Test
    @Timeout(1)
    void collectsTheScatteredElements() {
        assertThat("the scattered increasing elements are missed",
            new LongestIncreasingSubsequence.Dynamited()
                .longest(List.of(10, 9, 2, 5, 3, 7, 101, 18), Counter.create()), is(4));
    }

    @DisplayName("Перебор находит то же, что таблица")
    @Test
    @Timeout(5)
    void bruteAgreesWithTheTable() {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 50; attempt++) {
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < random.nextInt(12); i++) {
                values.add(random.nextInt(20));
            }
            if (new LongestIncreasingSubsequence.Recursion().longest(values, Counter.create())
                != new LongestIncreasingSubsequence.Dynamited().longest(values, Counter.create())) {
                ++mismatches;
            }
        }
        assertThat("the brute force search disagrees with the table", mismatches, is(0));
    }

    @DisplayName("Таблица обходится дешевле перебора")
    @Test
    @Timeout(5)
    void costsLessThanTheBruteForce() {
        List<Integer> values = List.of(10, 9, 2, 5, 3, 7, 101, 18, 4, 6, 12, 1);
        Counter table = Counter.create();
        Counter brute = Counter.create();
        new LongestIncreasingSubsequence.Dynamited().longest(values, table);
        new LongestIncreasingSubsequence.Recursion().longest(values, brute);
        assertThat("the table costs no less than the brute force search",
            table.count(), lessThan(brute.count()));
    }
}
