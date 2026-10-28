package ru.mifi.practice.vol3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/** Проверка генератора входных данных для сортировок. */
@DisplayName("Генератор чисел")
final class NumberGeneratorTest {

    @DisplayName("Последовательность получается заказанной длины")
    @Test
    @Timeout(5)
    void givesTheRequestedLength() {
        assertThat("the slice dont get the requested length", NumberGenerator.generateSlice(50).size(), is(50));
    }

    @DisplayName("Числа не выходят за верхнюю границу")
    @Test
    @Timeout(5)
    void staysWithinTheUpperBound() {
        boolean outside = false;
        for (Integer value : NumberGenerator.generateSlice(1000)) {
            outside |= value < 0 || value > NumberGenerator.MAX_GENERATED_ELEMENT_VALUE;
        }
        assertThat("a generated number escapes its bounds", outside, is(false));
    }

    @DisplayName("Пустая длина даёт пустую последовательность")
    @Test
    @Timeout(5)
    void givesAnEmptySliceForZeroLength() {
        assertThat("a zero length gives a non empty slice", NumberGenerator.generateSlice(0), is(List.of()));
    }

    @DisplayName("Последовательность не состоит из одного числа")
    @Test
    @Timeout(5)
    void variesTheGeneratedNumbers() {
        Set<Integer> distinct = new HashSet<>(NumberGenerator.generateSlice(1000));
        assertThat("the generator gives the same number over and over", distinct.size(), is(greaterThan(1)));
    }
}
