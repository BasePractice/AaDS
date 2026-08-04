package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

/** Проверка двоичного поиска по монотонной функции с заданной точностью. */
@DisplayName("Двоичный поиск по вещественному диапазону")
final class BinaryRealSearchTest {

    @DisplayName("Поиск находит аргумент, на котором функция даёт искомое")
    @Test
    @Timeout(1)
    void findsTheArgumentGivingTheValue() {
        assertThat("the search dont return the argument giving the value",
            new BinaryRealSearch(0.0001, false)
                .search(24.0, new Search.Range<>(10.0, 17.0), number -> 2 * number, Counter.create())
                .orElseThrow().doubleValue(), is(closeTo(12.0, 0.001)));
    }

    @DisplayName("Поиск находит корень из двух")
    @Test
    @Timeout(1)
    void findsTheSquareRootOfTwo() {
        assertThat("the search dont find the square root of two",
            new BinaryRealSearch(0.000001, false)
                .search(2.0, new Search.Range<>(0.0, 2.0), number -> number * number, Counter.create())
                .orElseThrow().doubleValue(), is(closeTo(Math.sqrt(2.0), 0.001)));
    }

    @DisplayName("Значение вне области значений не находится")
    @Test
    @Timeout(1)
    void findsNothingOutsideTheRange() {
        assertThat("a value outside the function range is found",
            new BinaryRealSearch(0.0001, false)
                .search(100.0, new Search.Range<>(10.0, 17.0), number -> 2 * number, Counter.create())
                .isPresent(), is(false));
    }
}
