package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Бинарный поиск по индексу")
final class BinaryIndexSearchTest {

    @DisplayName("Находит индекс первого элемента")
    @Test
    void findsTheFirstElement() {
        assertThat("first element dont get found",
            new BinaryIndexSearch(List.of(1, 3, 5, 7), false).search(1, new Counter.Default()).orElseThrow(),
            is(0));
    }

    @DisplayName("Находит индекс последнего элемента")
    @Test
    void findsTheLastElement() {
        assertThat("last element dont get found",
            new BinaryIndexSearch(List.of(1, 3, 5, 7), false).search(7, new Counter.Default()).orElseThrow(),
            is(3));
    }

    @DisplayName("Находит индекс элемента в середине")
    @Test
    void findsAnElementInTheMiddle() {
        assertThat("middle element dont get found",
            new BinaryIndexSearch(List.of(1, 3, 5, 7), false).search(5, new Counter.Default()).orElseThrow(),
            is(2));
    }

    @DisplayName("Отсутствующий элемент не находится")
    @Test
    void reportsMissingElement() {
        assertThat("a missing element is reported as found",
            new BinaryIndexSearch(List.of(1, 3, 5, 7), false).search(4, new Counter.Default()).isPresent(),
            is(false));
    }

    @DisplayName("Поиск в пустом списке ничего не находит")
    @Test
    void searchesEmptyListWithoutFailing() {
        assertThat("searching an empty list dont return an empty result",
            new BinaryIndexSearch(List.of(), false).search(1, new Counter.Default()).isPresent(), is(false));
    }

    @DisplayName("Поиск в списке из одного элемента")
    @Test
    void findsTheOnlyElement() {
        assertThat("the only element dont get found",
            new BinaryIndexSearch(List.of(1), false).search(1, new Counter.Default()).orElseThrow(), is(0));
    }
}
