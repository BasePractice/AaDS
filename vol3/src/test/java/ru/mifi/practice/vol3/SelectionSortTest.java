package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Сортировка выбором")
final class SelectionSortTest {

    @DisplayName("Упорядочивает элементы")
    @Test
    @Timeout(1)
    void ordersElements() {
        assertThat("selection sort dont order the elements",
            new SelectionSort<Integer>().sort(List.of(5, 3, 9, 1, 3), Counter.create(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Сравнивает одинаково на любом входе")
    @Test
    @Timeout(1)
    void comparesTheSameOnAnyInput() {
        Counter ordered = Counter.create();
        Counter reversed = Counter.create();
        new SelectionSort<Integer>().sort(List.of(1, 2, 3, 4, 5), ordered, false);
        new SelectionSort<Integer>().sort(List.of(5, 4, 3, 2, 1), reversed, false);
        assertThat("selection sort depends on the order of the input",
            ordered.count(), is(reversed.count()));
    }

    @DisplayName("Тратит n(n−1)/2 сравнений")
    @Test
    @Timeout(1)
    void spendsQuadraticComparisons() {
        Counter counter = Counter.create();
        new SelectionSort<Integer>().sort(List.of(5, 3, 9, 1, 3), counter, false);
        assertThat("selection sort spends other than ten comparisons on five elements",
            counter.count(), is(10));
    }

    @DisplayName("Неустойчива на равных ключах")
    @Test
    @Timeout(1)
    void breaksTheOrderOfEqualKeys() {
        List<Marked> input = List.of(new Marked(2, 'a'), new Marked(2, 'b'), new Marked(1, 'c'));
        assertThat("selection sort suddenly keeps the order of equal keys",
            new SelectionSort<Marked>().sort(input, Counter.create(), false),
            contains(new Marked(1, 'c'), new Marked(2, 'b'), new Marked(2, 'a')));
    }

    private record Marked(int key, char mark) implements Comparable<Marked> {
        @Override
        public int compareTo(Marked other) {
            return Integer.compare(key, other.key);
        }
    }
}
