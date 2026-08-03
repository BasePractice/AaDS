package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@DisplayName("Быстрая сортировка")
final class QuickSortTest {

    @DisplayName("Со случайным опорным упорядочивает элементы")
    @Test
    @Timeout(1)
    void randomlyOrdersElements() {
        assertThat("randomly pivoted quick sort dont order the elements",
            new QuickSort<>(new QuickSort.Strategy.Randomly<Integer>())
                .sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    /**
     * Задание к разделу «Сортировка»: в QuickSort есть ошибка, помеченная в JavaDoc класса.
     * Разбиение Хоара возвращает границу, а не итоговую позицию опорного элемента, поэтому
     * рекурсия по [low, middle) и [middle + 1, high) навсегда замораживает элемент middle.
     * Снимите @Disabled и добейтесь зелёного теста.
     */
    @Disabled("Учебное задание: ошибка в QuickSort со стратегией Halfway")
    @DisplayName("С разбиением Хоара упорядочивает элементы")
    @Test
    @Timeout(1)
    void halfwayOrdersElements() {
        assertThat("Hoare partitioning drops the pivot out of the recursion",
            new QuickSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }
}
