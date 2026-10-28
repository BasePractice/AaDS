package ru.mifi.practice.vol2.queens;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Восемь ферзей")
final class QueensTest {

    @DisplayName("Доска восемь на восемь держит девяносто два расположения")
    @Test
    @Timeout(5)
    void findsNinetyTwoArrangements() {
        assertThat("the eight queens puzzle has other than 92 solutions",
            new Queens(8).solutions(Counter.create()).size(), is(92));
    }

    @DisplayName("Один ферзь на клетке стоит без соседей")
    @Test
    @Timeout(1)
    void placesTheSingleQueen() {
        assertThat("the single queen dont fit the single cell",
            new Queens(1).solutions(Counter.create()).size(), is(1));
    }

    @DisplayName("На доске три на три ферзи не расходятся")
    @Test
    @Timeout(1)
    void findsNoArrangementOnTheSmallBoard() {
        assertThat("the three by three board suddenly holds three queens",
            new Queens(3).solutions(Counter.create()).size(), is(0));
    }

    @DisplayName("Ферзи не бьют друг друга ни по строке, ни по диагонали")
    @Test
    @Timeout(5)
    void keepsTheQueensApart() {
        assertThat("two queens attack each other", attacks(new Queens(8).solutions(Counter.create())), is(0));
    }

    @DisplayName("Отсечение бьёт полный перебор клеток")
    @Test
    @Timeout(5)
    void cutsTheSearchShort() {
        Counter counter = Counter.create();
        new Queens(8).solutions(counter);
        assertThat("the search walks as many cells as the plain enumeration",
            counter.count(), lessThan(100_000));
    }

    private static int attacks(List<List<Integer>> solutions) {
        int attacks = 0;
        for (List<Integer> queens : solutions) {
            for (int row = 0; row < queens.size(); row++) {
                for (int other = row + 1; other < queens.size(); other++) {
                    int distance = Math.abs(queens.get(row) - queens.get(other));
                    if (distance == 0 || distance == other - row) {
                        ++attacks;
                    }
                }
            }
        }
        return attacks;
    }
}
