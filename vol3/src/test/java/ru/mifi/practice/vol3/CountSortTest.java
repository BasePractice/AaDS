package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Сортировка подсчётом")
final class CountSortTest {

    @DisplayName("Упорядочивает элементы")
    @Test
    @Timeout(1)
    void ordersElements() {
        assertThat("count sort dont order the elements",
            new CountSort(9).sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Не меняет длину")
    @Test
    @Timeout(1)
    void keepsTheLength() {
        assertThat("count sort changes the number of elements",
            new CountSort(9).sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false).size(), is(5));
    }

    @DisplayName("На пустом входе даёт пустой результат")
    @Test
    @Timeout(1)
    void handlesEmptyInput() {
        assertThat("empty input dont produce an empty result",
            new CountSort(9).sort(List.of(), new Counter.Default(), false).size(), is(0));
    }
}
