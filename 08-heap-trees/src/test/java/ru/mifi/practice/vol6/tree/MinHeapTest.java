package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Задание слайда 437: куча на массиве. Реализуйте MinHeap.Array, снимите {@code @Disabled} и
 * добейтесь зелёных тестов.
 */
@DisplayName("Куча на массиве")
final class MinHeapTest {

    @Disabled("Учебное задание слайда 437: куча на массиве")
    @DisplayName("Десять тысяч случайных чисел извлекаются по возрастанию")
    @Test
    @Timeout(1)
    void drainsTenThousandRandomNumbersInAscendingOrder() {
        Random random = new Random();
        MinHeap<Integer> heap = new MinHeap.Array<Integer>(List.of(), Comparator.naturalOrder());
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            int value = random.nextInt();
            heap.add(value);
            expected.add(value);
        }
        expected.sort(Comparator.naturalOrder());
        List<Integer> drained = new ArrayList<>();
        while (heap.size() > 0) {
            drained.add(heap.poll());
        }
        assertThat("the heap dont drain in ascending order", drained, is(expected));
    }

    @Disabled("Учебное задание слайда 437: сборка кучи из готового массива")
    @DisplayName("Куча из готового массива держит минимум в корне")
    @Test
    @Timeout(1)
    void keepsTheMinimumOfABuiltHeapOnTop() {
        assertThat("a heap built from a list dont hold its minimum on top",
            new MinHeap.Array<>(List.of(8, 3, 9, 1, 7), Comparator.<Integer>naturalOrder()).peek(), is(1));
    }

    @Disabled("Учебное задание слайда 437: max-heap через Comparator")
    @DisplayName("С обратным порядком в корне лежит максимум")
    @Test
    @Timeout(1)
    void keepsTheMaximumOnTopInReverseOrder() {
        assertThat("a reversed comparator dont give a max-heap",
            new MinHeap.Array<>(List.of(8, 3, 9, 1, 7), Comparator.<Integer>reverseOrder()).peek(), is(9));
    }
}
