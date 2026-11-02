package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Перестановки Хипа")
final class HeapPermutationsTest {

    @DisplayName("Перестановки идут в порядке Хипа")
    @Test
    @Timeout(1)
    void generatesPermutationsInHeapOrder() {
        assertThat("permutations dont come out in Heap order",
            Combinatorics.collect(HeapPermutations.of(3)),
            contains("[1, 2, 3]", "[2, 1, 3]", "[3, 1, 2]", "[1, 3, 2]", "[2, 3, 1]", "[3, 2, 1]"));
    }

    @DisplayName("Количество перестановок равно факториалу")
    @Test
    @Timeout(1)
    void generatesFactorialNumberOfPermutations() {
        assertThat("the number of Heap permutations dont match 4!",
            collect(4).size(), is(24));
    }

    @DisplayName("Перестановки не повторяются")
    @Test
    @Timeout(1)
    void generatesDistinctPermutations() {
        List<int[]> permutations = collect(5);
        Set<String> unique = new HashSet<>();
        for (int[] permutation : permutations) {
            unique.add(Arrays.toString(permutation));
        }
        assertThat("some Heap permutations are generated twice",
            unique.size(), is(permutations.size()));
    }

    @DisplayName("Нулевой размер отвергается")
    @Test
    @Timeout(1)
    void cannotGeneratePermutationsOfZeroSize() {
        assertThrows(IllegalArgumentException.class, () -> HeapPermutations.of(0),
            "zero size is accepted");
    }

    private static List<int[]> collect(int size) {
        List<int[]> collected = new ArrayList<>();
        for (int[] permutation : HeapPermutations.of(size)) {
            collected.add(permutation);
        }
        return collected;
    }
}
