package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Линейный поиск")
final class LinearSearchTest {

    @DisplayName("Находит элемент в неупорядоченном списке")
    @Test
    @Timeout(1)
    void findsAnElementInAnUnsortedList() {
        assertThat("an element of an unsorted list dont get found",
            new LinearSearch(List.of(7, 1, 5, 3)).search(5, new Counter.Default()).orElseThrow(), is(2));
    }

    @DisplayName("Возвращает первое из повторяющихся вхождений")
    @Test
    @Timeout(1)
    void returnsTheFirstOfRepeatedOccurrences() {
        assertThat("a later duplicate is returned instead of the first one",
            new LinearSearch(List.of(4, 9, 4, 9)).search(9, new Counter.Default()).orElseThrow(), is(1));
    }

    @DisplayName("Отсутствующий элемент не находится")
    @Test
    @Timeout(1)
    void reportsMissingElement() {
        assertThat("a missing element is reported as found",
            new LinearSearch(List.of(7, 1, 5, 3)).search(4, new Counter.Default()).isPresent(), is(false));
    }

    @DisplayName("Поиск в пустом списке ничего не находит")
    @Test
    @Timeout(1)
    void searchesEmptyListWithoutFailing() {
        assertThat("searching an empty list dont return an empty result",
            new LinearSearch(List.of()).search(1, new Counter.Default()).isPresent(), is(false));
    }

    @DisplayName("Ради отсутствующего элемента просматривает весь список")
    @Test
    @Timeout(1)
    void scansTheWholeListForAMissingElement() {
        Counter counter = new Counter.Default();
        new LinearSearch(List.of(7, 1, 5, 3, 8)).search(4, counter);
        assertThat("a missing element is reported before every element is checked", counter.count(), is(5));
    }
}
