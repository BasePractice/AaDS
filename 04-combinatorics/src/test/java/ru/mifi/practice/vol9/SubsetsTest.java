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

@DisplayName("Подмножества")
final class SubsetsTest {

    @DisplayName("Порождает все подмножества")
    @Test
    @Timeout(1)
    void generatesEverySubset() {
        assertThat("the number of subsets dont equal two to the size",
            collect(3).size(), is(8));
    }

    @DisplayName("Подмножества идут по битовым маскам")
    @Test
    @Timeout(1)
    void generatesSubsetsByBitMask() {
        assertThat("subsets dont follow the bit mask order",
            Combinatorics.collect(Subsets.of(2)),
            contains("[0, 0]", "[0, 1]", "[1, 0]", "[1, 1]"));
    }

    @DisplayName("Первое подмножество пустое")
    @Test
    @Timeout(1)
    void startsWithEmptySubset() {
        assertThat("the first subset is not empty", collect(3).get(0), is(new int[]{0, 0, 0}));
    }

    @DisplayName("Последнее подмножество содержит все элементы")
    @Test
    @Timeout(1)
    void endsWithFullSubset() {
        List<int[]> subsets = collect(3);
        assertThat("the last subset dont contain every element",
            subsets.get(subsets.size() - 1), is(new int[]{1, 1, 1}));
    }

    @DisplayName("Подмножества не повторяются")
    @Test
    @Timeout(1)
    void generatesDistinctSubsets() {
        List<int[]> subsets = collect(4);
        Set<String> unique = new HashSet<>();
        for (int[] subset : subsets) {
            unique.add(Arrays.toString(subset));
        }
        assertThat("some subsets are generated twice", unique.size(), is(subsets.size()));
    }

    private static List<int[]> collect(int size) {
        List<int[]> collected = new ArrayList<>();
        for (int[] subset : Subsets.of(size)) {
            collected.add(subset);
        }
        return collected;
    }
}
