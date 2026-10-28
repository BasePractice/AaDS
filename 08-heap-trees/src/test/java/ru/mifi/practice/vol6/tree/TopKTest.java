package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Задание слайда 476: топ-K из потока. Реализуйте TopK.Bounded, снимите {@code @Disabled} и
 * добейтесь зелёных тестов.
 */
@DisplayName("Топ-K из потока")
final class TopKTest {

    @Disabled("Учебное задание слайда 476: топ-K из потока")
    @DisplayName("Находит сто самых больших чисел из миллиона")
    @Test
    @Timeout(5)
    void findsTheHundredLargestOfAMillion() {
        List<Integer> stream = new Random().ints(1_000_000).boxed().collect(Collectors.toList());
        List<Integer> expected = stream.stream().sorted(Comparator.reverseOrder()).limit(100).collect(Collectors.toList());
        assertThat("the hundred largest numbers dont match a full sort",
            new TopK.Bounded().largest(stream.iterator(), 100), is(expected));
    }

    @Disabled("Учебное задание слайда 476: топ-K из короткого потока")
    @DisplayName("Поток короче k отдаётся целиком по убыванию")
    @Test
    @Timeout(1)
    void returnsAShortStreamWhole() {
        assertThat("a stream shorter than k dont come back whole",
            new TopK.Bounded().largest(List.of(4, 9, 1).iterator(), 5), contains(9, 4, 1));
    }
}
