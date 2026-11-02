package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Сравнение алгоритмов перестановок")
final class PermutationAlgorithmsTest {
    private static final int SIZE = 5;

    @DisplayName("Все алгоритмы выдают одно и то же множество перестановок")
    @Test
    @Timeout(1)
    void allAlgorithmsProduceTheSameSet() {
        List<int[]> lexicographic = list(Combinatorics.permutationsOf(SIZE, Counter.create()));
        List<int[]> heap = list(HeapPermutations.of(SIZE, Counter.create()));
        List<int[]> johnson = list(JohnsonTrotter.permutations(SIZE, Counter.create()));

        Set<String> expected = normalized(lexicographic);
        assertThat("Combinatorics produces duplicates", lexicographic.size(), is(expected.size()));
        assertThat("HeapPermutations produces duplicates", heap.size(), is(expected.size()));
        assertThat("JohnsonTrotter produces duplicates", johnson.size(), is(expected.size()));
        assertThat("HeapPermutations produces another set of permutations",
            normalized(heap), is(expected));
        assertThat("JohnsonTrotter produces another set of permutations",
            normalized(johnson), is(expected));
    }

    private static List<int[]> list(Iterable<int[]> permutations) {
        List<int[]> result = new ArrayList<>();
        for (int[] permutation : permutations) {
            result.add(permutation);
        }
        return result;
    }

    private static Set<String> normalized(List<int[]> permutations) {
        Set<String> result = new HashSet<>();
        for (int[] permutation : permutations) {
            int[] normalized = permutation.clone();
            int min = Arrays.stream(normalized).min().orElse(0);
            for (int i = 0; i < normalized.length; i++) {
                normalized[i] -= min;
            }
            result.add(Arrays.toString(normalized));
        }
        return result;
    }
}
