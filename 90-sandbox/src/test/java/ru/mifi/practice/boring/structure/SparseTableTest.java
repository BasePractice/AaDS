package ru.mifi.practice.boring.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Разреженная таблица")
final class SparseTableTest {

    @DisplayName("Находит минимум на отрезке")
    @Test
    @Timeout(1)
    void findsTheMinimumOfTheSegment() {
        assertThat("the minimum of the segment is wrong",
            new SparseTable(new int[]{5, 3, 9, 1, 7}).minimum(0, 3), is(3));
    }

    @DisplayName("Находит минимум всего массива")
    @Test
    @Timeout(1)
    void findsTheMinimumOfTheWholeArray() {
        assertThat("the minimum of the whole array is wrong",
            new SparseTable(new int[]{5, 3, 9, 1, 7}).minimum(0, 5), is(1));
    }

    @DisplayName("Отрезок из одного числа даёт это число")
    @Test
    @Timeout(1)
    void findsTheSingleValue() {
        assertThat("the single value segment is wrong",
            new SparseTable(new int[]{5, 3, 9}).minimum(1, 2), is(3));
    }

    @DisplayName("Отрезки нечётной длины накрываются с перехлёстом")
    @Test
    @Timeout(1)
    void coversTheOddLengthWithAnOverlap() {
        assertThat("the overlap of two covering blocks breaks the answer",
            new SparseTable(new int[]{8, 2, 6, 4, 9, 3, 7}).minimum(1, 6), is(2));
    }

    @DisplayName("Пустой отрезок отвергается сразу")
    @Test
    @Timeout(1)
    void refusesTheEmptySegment() {
        assertThat("the empty segment gets an answer",
            assertThrows(IllegalArgumentException.class,
                () -> new SparseTable(new int[]{1, 2}).minimum(1, 1)).getMessage(),
            is("Отрезок от 1 до 1 пуст, минимума в нём нет"));
    }

    @DisplayName("Отвечает так же, как прямой перебор отрезка")
    @Test
    @Timeout(5)
    void agreesWithThePlainWalk() {
        Random random = new Random(20260815L);
        int[] values = new int[100];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(1000);
        }
        SparseTable table = new SparseTable(values);
        int mismatches = 0;
        for (int attempt = 0; attempt < 500; attempt++) {
            int from = random.nextInt(values.length);
            int to = from + 1 + random.nextInt(values.length - from);
            int expected = Integer.MAX_VALUE;
            for (int i = from; i < to; i++) {
                expected = Math.min(expected, values[i]);
            }
            if (table.minimum(from, to) != expected) {
                ++mismatches;
            }
        }
        assertThat("the sparse table disagrees with the plain walk", mismatches, is(0));
    }
}
