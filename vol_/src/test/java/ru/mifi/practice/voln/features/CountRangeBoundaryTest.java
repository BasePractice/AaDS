package ru.mifi.practice.voln.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Границы диапазонов счётчика")
final class CountRangeBoundaryTest {

    @DisplayName("Значение внутри диапазона даёт награду этого диапазона")
    @Test
    void rewardsAValueInsideARange() {
        assertThat("a value inside a range dont get its reward", reward(101), is(1001L));
    }

    @DisplayName("Значение ровно на внутренней границе не проваливается в нулевой диапазон")
    @Test
    void keepsAValueOnAnInnerBoundaryInsideItsRange() {
        assertThat("a value exactly on an inner boundary falls through to the first range",
            reward(200), is(1001L));
    }

    @DisplayName("Значение сразу за внутренней границей переходит в следующий диапазон")
    @Test
    void movesToTheNextRangeJustAfterABoundary() {
        assertThat("crossing the boundary dont move to the next range", reward(201), is(1002L));
    }

    @DisplayName("Значение выше последней границы остаётся в последнем диапазоне")
    @Test
    void staysInTheLastRangeAboveTheLastBoundary() {
        assertThat("a value above the last boundary leaves the last range", reward(1000), is(1002L));
    }

    private static long reward(long value) {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067, value);
        return count.countValue(countId, 10067).orElseThrow();
    }
}
