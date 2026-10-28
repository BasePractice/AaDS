package ru.mifi.practice.voln.jt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Джонсон Троттер")
final class JohnsonTrotterTest {

    @Test
    @Timeout(5)
    @DisplayName("Не правильное количество перестановок")
    void zeroSizeThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> JohnsonTrotter.permutations(0).iterator().next(),
            "zero size permutations dont throw");
    }

    @Test
    @Timeout(5)
    @DisplayName("Единичная перестановка: количество")
    void singlePermutationCount() {
        int count = 0;
        for (int[] ignored : JohnsonTrotter.permutations(1)) {
            count++;
        }
        assertThat("permutations of size one dont form a single result", count, is(1));
    }

    @Test
    @Timeout(5)
    @DisplayName("Единичная перестановка: содержимое")
    void singlePermutationContent() {
        List<int[]> list = new ArrayList<>();
        for (int[] permutation : JohnsonTrotter.permutations(1)) {
            list.add(permutation);
        }
        assertThat("single permutation content is wrong", list.get(0), is(new int[]{1}));
    }

    @ParameterizedTest
    @Timeout(5)
    @ValueSource(ints = {2, 3, 4})
    @DisplayName("Количество перестановок равно факториалу")
    void permutationCount(int n) {
        int expected = 1;
        for (int i = 2; i <= n; i++) {
            expected *= i;
        }
        int count = 0;
        for (int[] ignored : JohnsonTrotter.permutations(n)) {
            count++;
        }
        assertThat("permutations count for " + n + " doesnt equal factorial", count, is(expected));
    }

    @ParameterizedTest
    @Timeout(5)
    @ValueSource(ints = {2, 3, 4})
    @DisplayName("Каждый результат — перестановка 1..n")
    void allAreValidPermutations(int n) {
        boolean allValid = true;
        for (int[] permutation : JohnsonTrotter.permutations(n)) {
            boolean valid = permutation.length == n;
            boolean[] seen = new boolean[n + 1];
            for (int v : permutation) {
                if (v < 1 || v > n || seen[v]) {
                    valid = false;
                    break;
                }
                seen[v] = true;
            }
            if (!valid) {
                allValid = false;
            }
        }
        assertThat("some result for " + n + " is not a permutation of one to n", allValid, is(true));
    }

    @ParameterizedTest
    @Timeout(5)
    @ValueSource(ints = {2, 3, 4})
    @DisplayName("Все перестановки различны")
    void permutationsAreUnique(int n) {
        Set<String> unique = new HashSet<>();
        int count = 0;
        for (int[] permutation : JohnsonTrotter.permutations(n)) {
            count++;
            StringBuilder key = new StringBuilder(permutation.length * 2);
            for (int v : permutation) {
                if (!key.isEmpty()) {
                    key.append(',');
                }
                key.append(v);
            }
            unique.add(key.toString());
        }
        assertThat("permutations for " + n + " contain duplicates", unique.size(), is(count));
    }

    @Test
    @Timeout(5)
    @DisplayName("Первая перестановка — тождественная")
    void firstIsIdentity() {
        int[] first = null;
        for (int[] permutation : JohnsonTrotter.permutations(4)) {
            first = permutation;
            break;
        }
        assertThat("first permutation is not the identity", first, is(new int[]{1, 2, 3, 4}));
    }

    @Test
    @Timeout(5)
    @DisplayName("Соседние перестановки отличаются одним обменом")
    void consecutiveAreAdjacentSwaps() {
        List<int[]> list = new ArrayList<>();
        for (int[] permutation : JohnsonTrotter.permutations(4)) {
            list.add(permutation);
        }
        boolean allAdjacent = true;
        for (int i = 1; i < list.size(); i++) {
            int[] a = list.get(i - 1);
            int[] b = list.get(i);
            int diff = 0;
            int pos = -1;
            for (int j = 0; j < a.length; j++) {
                if (a[j] != b[j]) {
                    diff++;
                    if (pos < 0) {
                        pos = j;
                    }
                }
            }
            boolean adjacent = diff == 2 && pos + 1 < a.length
                && a[pos] == b[pos + 1] && a[pos + 1] == b[pos];
            if (!adjacent) {
                allAdjacent = false;
            }
        }
        assertThat("consecutive permutations are not adjacent swaps", allAdjacent, is(true));
    }
}
