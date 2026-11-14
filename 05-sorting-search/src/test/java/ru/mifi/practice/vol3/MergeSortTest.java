package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol3.sort.MergeSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Сортировка слиянием")
final class MergeSortTest {

    @DisplayName("Упорядочивает элементы")
    @Test
    @Timeout(1)
    void ordersElements() {
        assertThat("merge sort dont order the elements",
            new MergeSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false),
            contains(1, 3, 3, 5, 9));
    }

    @DisplayName("Не меняет длину")
    @Test
    @Timeout(1)
    void keepsTheLength() {
        assertThat("merge sort changes the number of elements",
            new MergeSort<Integer>().sort(List.of(5, 3, 9, 1, 3), new Counter.Default(), false).size(), is(5));
    }

    @DisplayName("Устойчива")
    @Test
    @Timeout(1)
    void isStable() {
        List<Marked> input = List.of(new Marked(1, 'a'), new Marked(1, 'b'), new Marked(0, 'c'));
        assertThat("equal elements swap their original order",
            new MergeSort<Marked>().sort(input, new Counter.Default(), false),
            contains(new Marked(0, 'c'), new Marked(1, 'a'), new Marked(1, 'b')));
    }

    @DisplayName("Возвращает новый список")
    @Test
    @Timeout(1)
    void doesNotAliasSingletonInput() {
        List<Integer> input = List.of(1);
        assertThat("single element input is returned by reference",
            new MergeSort<Integer>().sort(input, new Counter.Default(), false) == input, is(false));
    }

    @Test
    void ordersLectureExample() {
        List<Integer> input = new ArrayList<>(List.of(7, 8, 2, 0, 5, 2, 7, 0));
        assertThat(new MergeSort<Integer>().sort(input, Counter.create(), false),
            contains(0, 0, 2, 2, 5, 7, 7, 8));
    }

    @Test
    void countsTransfersForPowersOfTwo() {
        List<Integer> counts = Stream.of(1, 2, 4, 8, 16).map(size -> {
            Counter counter = Counter.create();
            new MergeSort<Integer>().sort(new ArrayList<>(Collections.nCopies(size, 7)), counter, false);
            return counter.count();
        }).toList();
        assertThat(counts, contains(0, 4, 16, 48, 128));
    }

    private record Marked(int key, char mark) implements Comparable<Marked> {
        @Override
        public int compareTo(Marked other) {
            return Integer.compare(key, other.key);
        }
    }
}
