package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Дерево Фенвика")
final class FenwickTreeTest {

    @DisplayName("Считает сумму первых элементов")
    @Test
    @Timeout(1)
    void countsThePrefix() {
        assertThat("the prefix sum is wrong",
            new FenwickTree(new int[]{3, 1, 4, 1, 5, 9}).prefix(3), is(8));
    }

    @DisplayName("Считает сумму на отрезке")
    @Test
    @Timeout(1)
    void countsTheSegment() {
        assertThat("the sum on the segment is wrong",
            new FenwickTree(new int[]{3, 1, 4, 1, 5, 9}).sum(1, 4), is(6));
    }

    @DisplayName("Пустой префикс равен нулю")
    @Test
    @Timeout(1)
    void countsTheEmptyPrefixAsZero() {
        assertThat("the empty prefix is not zero", new FenwickTree(new int[]{3, 1, 4}).prefix(0), is(0));
    }

    @DisplayName("Учитывает прибавку в последующих запросах")
    @Test
    @Timeout(1)
    void followsTheAddedValue() {
        FenwickTree tree = new FenwickTree(new int[]{3, 1, 4, 1, 5, 9});
        tree.add(2, 6);
        assertThat("the added value is missing from the sum", tree.sum(1, 4), is(12));
    }

    @DisplayName("Отвечает так же, как прямой перебор отрезка")
    @Test
    @Timeout(5)
    void agreesWithThePlainWalk() {
        Random random = new Random(20260815L);
        int[] values = new int[64];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(100);
        }
        FenwickTree tree = new FenwickTree(values);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            int from = random.nextInt(values.length);
            int to = from + random.nextInt(values.length - from);
            int expected = 0;
            for (int i = from; i < to; i++) {
                expected += values[i];
            }
            if (tree.sum(from, to) != expected) {
                ++mismatches;
            }
        }
        assertThat("the fenwick tree disagrees with the plain walk", mismatches, is(0));
    }
}
