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

@DisplayName("Джонсон — Троттер")
final class JohnsonTrotterTest {

    @DisplayName("Нулевой размер отвергается")
    @Test
    @Timeout(1)
    void cannotGeneratePermutationsOfZeroSize() {
        assertThrows(IllegalArgumentException.class, () -> JohnsonTrotter.permutations(0),
            "zero size is accepted");
    }

    @DisplayName("Единичный набор даёт одну перестановку")
    @Test
    @Timeout(1)
    void generatesSinglePermutationOfOneElement() {
        assertThat("a one element set dont have a single permutation",
            Combinatorics.collect(JohnsonTrotter.permutations(1)), contains("[1]"));
    }

    @DisplayName("Количество перестановок равно факториалу")
    @Test
    @Timeout(1)
    void generatesFactorialNumberOfPermutations() {
        assertThat("the number of permutations dont match 4!",
            collect(4).size(), is(24));
    }

    @DisplayName("Перестановки идут в порядке Джонсона — Троттера")
    @Test
    @Timeout(1)
    void generatesPermutationsInJohnsonTrotterOrder() {
        assertThat("permutations dont come out in Johnson Trotter order",
            Combinatorics.collect(JohnsonTrotter.permutations(3)),
            contains("[1, 2, 3]", "[1, 3, 2]", "[3, 1, 2]", "[3, 2, 1]", "[2, 3, 1]", "[2, 1, 3]"));
    }

    @DisplayName("Первая перестановка тождественная")
    @Test
    @Timeout(1)
    void startsWithIdentityPermutation() {
        assertThat("the first permutation is not the identity",
            collect(4).get(0), is(new int[]{1, 2, 3, 4}));
    }

    @DisplayName("Каждый результат — перестановка чисел от единицы до размера")
    @Test
    @Timeout(1)
    void generatesOnlyPermutationsOfOneToSize() {
        boolean valid = true;
        for (int[] permutation : collect(4)) {
            valid &= permutation(permutation);
        }
        assertThat("some result is not a permutation of one to size", valid, is(true));
    }

    @DisplayName("Перестановки не повторяются")
    @Test
    @Timeout(1)
    void generatesDistinctPermutations() {
        List<int[]> permutations = collect(4);
        Set<String> unique = new HashSet<>();
        for (int[] permutation : permutations) {
            unique.add(Arrays.toString(permutation));
        }
        assertThat("some permutations are generated twice", unique.size(), is(permutations.size()));
    }

    @DisplayName("Соседние перестановки отличаются одним обменом")
    @Test
    @Timeout(1)
    void swapsOnlyNeighboursBetweenPermutations() {
        List<int[]> permutations = collect(4);
        int distant = 0;
        for (int i = 1; i < permutations.size(); i++) {
            if (!neighbours(permutations.get(i - 1), permutations.get(i))) {
                ++distant;
            }
        }
        assertThat("consecutive permutations dont differ by one adjacent swap", distant, is(0));
    }

    private static List<int[]> collect(int size) {
        List<int[]> collected = new ArrayList<>();
        for (int[] permutation : JohnsonTrotter.permutations(size)) {
            collected.add(permutation);
        }
        return collected;
    }

    private static boolean permutation(int[] values) {
        boolean[] seen = new boolean[values.length + 1];
        boolean valid = true;
        for (int value : values) {
            if (value < 1 || value > values.length || seen[value]) {
                valid = false;
                break;
            }
            seen[value] = true;
        }
        return valid;
    }

    private static boolean neighbours(int[] left, int[] right) {
        int first = -1;
        int differences = 0;
        for (int i = 0; i < left.length; i++) {
            if (left[i] != right[i]) {
                if (first < 0) {
                    first = i;
                }
                ++differences;
            }
        }
        return differences == 2 && first + 1 < left.length
            && left[first] == right[first + 1] && left[first + 1] == right[first];
    }
}
