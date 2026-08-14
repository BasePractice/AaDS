package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Дерево отрезков")
final class SegmentTreeTest {

    @DisplayName("Складывает значения на отрезке")
    @Test
    @Timeout(1)
    void foldsTheSegment() {
        assertThat("the sum on the segment is wrong",
            new SegmentTree(new int[]{3, 1, 4, 1, 5, 9}, Integer::sum, 0).fold(1, 4), is(6));
    }

    @DisplayName("Складывает весь массив")
    @Test
    @Timeout(1)
    void foldsTheWholeArray() {
        assertThat("the sum of the whole array is wrong",
            new SegmentTree(new int[]{3, 1, 4, 1, 5, 9}, Integer::sum, 0).fold(0, 6), is(23));
    }

    @DisplayName("Отдаёт нейтральный элемент на пустом отрезке")
    @Test
    @Timeout(1)
    void foldsTheEmptySegmentToTheNeutral() {
        assertThat("the empty segment folds to something other than the neutral",
            new SegmentTree(new int[]{3, 1, 4}, Integer::sum, 0).fold(2, 2), is(0));
    }

    @DisplayName("Учитывает изменение значения в последующих запросах")
    @Test
    @Timeout(1)
    void followsTheChangedValue() {
        SegmentTree tree = new SegmentTree(new int[]{3, 1, 4, 1, 5, 9}, Integer::sum, 0);
        tree.set(2, 10);
        assertThat("the changed value is missing from the sum", tree.fold(1, 4), is(12));
    }

    @DisplayName("Ищет минимум на отрезке той же свёрткой")
    @Test
    @Timeout(1)
    void foldsTheSegmentToItsMinimum() {
        assertThat("the minimum on the segment is wrong",
            new SegmentTree(new int[]{3, 1, 4, 1, 5, 9}, Math::min, Integer.MAX_VALUE).fold(2, 6), is(1));
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
        SegmentTree tree = new SegmentTree(values, Integer::sum, 0);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            int from = random.nextInt(values.length);
            int to = from + random.nextInt(values.length - from);
            int expected = 0;
            for (int i = from; i < to; i++) {
                expected += values[i];
            }
            if (tree.fold(from, to) != expected) {
                ++mismatches;
            }
        }
        assertThat("the segment tree disagrees with the plain walk", mismatches, is(0));
    }
}
