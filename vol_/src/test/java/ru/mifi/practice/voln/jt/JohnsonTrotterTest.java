package ru.mifi.practice.voln.jt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Джонсон Троттер")
class JohnsonTrotterTest {
    private static List<int[]> collect(Iterable<int[]> it) {
        List<int[]> list = new ArrayList<>();
        for (int[] ints : it) {
            list.add(ints);
        }
        return list;
    }

    private static int fact(int n) {
        int r = 1;
        for (int i = 2; i <= n; i++) {
            r *= i;
        }
        return r;
    }

    private static String key(int[] a) {
        StringBuilder b = new StringBuilder(a.length * 2);
        for (int v : a) {
            if (!b.isEmpty()) {
                b.append(',');
            }
            b.append(v);
        }
        return b.toString();
    }

    private static boolean isPermutationOf(int n, int[] a) {
        if (a.length != n) {
            return false;
        }
        boolean[] seen = new boolean[n + 1];
        for (int v : a) {
            if (v < 1 || v > n || seen[v]) {
                return false;
            }
            seen[v] = true;
        }
        return true;
    }

    private static int[] identity(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i + 1;
        }
        return a;
    }

    private static boolean isAdjacentSwap(int[] a, int[] b) {
        if (a.length != b.length) {
            return false;
        }
        int n = a.length;
        int diff = 0;
        int pos = -1;
        for (int i = 0; i < n; i++) {
            if (a[i] != b[i]) {
                diff++;
                if (pos < 0) {
                    pos = i;
                }
            }
        }
        if (diff != 2) {
            return false;
        }
        if (pos + 1 >= n) {
            return false;
        }
        return a[pos] == b[pos + 1] && a[pos + 1] == b[pos];
    }

    @Test
    @DisplayName("Не правильное количество перестановок")
    void invalidN() {
        assertThrows(IllegalArgumentException.class, () -> JohnsonTrotter.permutations(0).iterator().next());
    }

    @Test
    @DisplayName("Единичная перестановка")
    void n1() {
        List<int[]> list = collect(JohnsonTrotter.permutations(1));
        assertEquals(1, list.size());
        assertArrayEquals(new int[]{1}, list.get(0));
    }

    @Test
    @DisplayName("Перестановка с 2 до 4")
    void countAndUniqueness() {
        for (int n = 2; n <= 4; n++) {
            List<int[]> list = collect(JohnsonTrotter.permutations(n));
            assertEquals(fact(n), list.size());
            Set<String> uniq = new HashSet<>();
            for (int[] p : list) {
                uniq.add(key(p));
                assertTrue(isPermutationOf(n, p));
            }
            assertEquals(list.size(), uniq.size());
        }
    }

    @Test
    @DisplayName("Проверка с перестановками")
    void orderProperty() {
        int n = 4;
        List<int[]> list = collect(JohnsonTrotter.permutations(n));
        assertArrayEquals(identity(n), list.get(0));
        for (int i = 1; i < list.size(); i++) {
            assertTrue(isAdjacentSwap(list.get(i - 1), list.get(i)));
        }
    }
}
