package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Пузырьковая сортировка")
final class BubbleSortTest {

    @DisplayName("Упорядочивает элементы")
    @Test
    @Timeout(1)
    void ordersElements() {
        assertThat("bubble sort dont order the elements",
            new BubbleSort<Integer>().sort(List.of(5, 3, 9, 1, 3), Counter.create(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Устойчива")
    @Test
    @Timeout(1)
    void isStable() {
        List<Marked> input = List.of(new Marked(1, 'a'), new Marked(1, 'b'), new Marked(0, 'c'));
        assertThat("equal elements swap their original order",
            new BubbleSort<Marked>().sort(input, Counter.create(), false),
            contains(new Marked(0, 'c'), new Marked(1, 'a'), new Marked(1, 'b')));
    }

    @DisplayName("Обрывает проходы на упорядоченном входе")
    @Test
    @Timeout(1)
    void stopsOnTheOrderedInput() {
        Counter counter = Counter.create();
        new BubbleSort<Integer>().sort(List.of(1, 2, 3, 4, 5), counter, false);
        assertThat("bubble sort walks the ordered input more than once", counter.count(), is(4));
    }

    @DisplayName("На развёрнутом входе шагов больше, чем на упорядоченном")
    @Test
    @Timeout(1)
    void costsMoreOnTheReversedInput() {
        Counter ordered = Counter.create();
        Counter reversed = Counter.create();
        new BubbleSort<Integer>().sort(List.of(1, 2, 3, 4, 5), ordered, false);
        new BubbleSort<Integer>().sort(List.of(5, 4, 3, 2, 1), reversed, false);
        assertThat("the reversed input costs no more than the ordered one",
            ordered.count(), lessThan(reversed.count()));
    }

    private record Marked(int key, char mark) implements Comparable<Marked> {
        @Override
        public int compareTo(Marked other) {
            return Integer.compare(key, other.key);
        }
    }
}
