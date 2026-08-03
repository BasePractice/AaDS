package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Пирамидальная сортировка")
final class HeapSortTest {

    @DisplayName("Упорядочивает элементы")
    @Test
    @Timeout(1)
    void ordersElements() {
        assertThat("heap sort dont order the elements",
            new HeapSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Не меняет длину")
    @Test
    @Timeout(1)
    void keepsTheLength() {
        assertThat("heap sort changes the number of elements",
            new HeapSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false).size(), is(5));
    }

    @DisplayName("Справляется с пустым входом")
    @Test
    @Timeout(1)
    void handlesEmptyInput() {
        assertThat("empty input dont produce an empty result",
            new HeapSort<Integer>().sort(List.of(), new Counter.Default(), false).size(), is(0));
    }
}
