package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Сортировка вставками")
final class InsertionSortTest {

    @DisplayName("Упорядочивает элементы")
    @Test
    @Timeout(1)
    void ordersElements() {
        assertThat("insertion sort dont order the elements",
            new InsertionSort<Integer>().sort(List.of(5, 3, 9, 1, 3), Counter.create(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Устойчива")
    @Test
    @Timeout(1)
    void isStable() {
        List<Marked> input = List.of(new Marked(1, 'a'), new Marked(1, 'b'), new Marked(0, 'c'));
        assertThat("equal elements swap their original order",
            new InsertionSort<Marked>().sort(input, Counter.create(), false),
            contains(new Marked(0, 'c'), new Marked(1, 'a'), new Marked(1, 'b')));
    }

    @DisplayName("Проходит упорядоченный вход за линейное число шагов")
    @Test
    @Timeout(1)
    void walksTheOrderedInputLinearly() {
        Counter counter = Counter.create();
        new InsertionSort<Integer>().sort(List.of(1, 2, 3, 4, 5), counter, false);
        assertThat("insertion sort compares the ordered input more than n times", counter.count(), is(4));
    }

    @DisplayName("Не меняет исходный список")
    @Test
    @Timeout(1)
    void keepsTheSourceIntact() {
        List<Integer> input = new ArrayList<>(List.of(5, 3, 9));
        new InsertionSort<Integer>().sort(input, Counter.create(), false);
        assertThat("insertion sort reorders the source list", input, contains(5, 3, 9));
    }

    private record Marked(int key, char mark) implements Comparable<Marked> {
        @Override
        public int compareTo(Marked other) {
            return Integer.compare(key, other.key);
        }
    }
}
