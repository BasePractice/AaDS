package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/** Проверка двоичного поиска по монотонной функции на натуральном диапазоне. */
@DisplayName("Двоичный поиск по натуральному диапазону")
final class BinaryNaturalSearchTest {

    @DisplayName("Поиск находит аргумент, на котором функция даёт искомое")
    @Test
    @Timeout(1)
    void findsTheArgumentGivingTheValue() {
        assertThat("the search dont return the argument giving the value",
            new BinaryNaturalSearch(false)
                .search(24L, new Search.Range<>(10L, 17L), number -> 2 * number, Counter.create())
                .orElseThrow(), is(12L));
    }

    @DisplayName("Отсутствующее значение не находится")
    @Test
    @Timeout(1)
    void findsNothingForAMissingValue() {
        assertThat("a value outside the function range is found",
            new BinaryNaturalSearch(false)
                .search(25L, new Search.Range<>(10L, 17L), number -> 2 * number, Counter.create())
                .isPresent(), is(false));
    }

    @DisplayName("Поиск по диапазону в тысячу шагов укладывается в десять сравнений")
    @Test
    @Timeout(1)
    void staysLogarithmic() {
        Counter counter = Counter.create();
        new BinaryNaturalSearch(false)
            .search(1000L, new Search.Range<>(0L, 1024L), number -> number, counter);
        assertThat("the search over a thousand steps costs more than ten comparisons",
            counter.count(), is(lessThanOrEqualTo(10)));
    }
}
