package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Беспорядки")
final class DerangementsTest {

    @DisplayName("Четыре элемента имеют девять беспорядков")
    @Test
    @Timeout(1)
    void countsFourDerangements() {
        assertThat("the number of derangements of four elements dont equal 9",
            Derangements.number(4), is(BigInteger.valueOf(9)));
    }

    @DisplayName("Пять элементов имеют сорок четыре беспорядка")
    @Test
    @Timeout(1)
    void countsFiveDerangements() {
        assertThat("the number of derangements of five elements dont equal 44",
            Derangements.number(5), is(BigInteger.valueOf(44)));
    }

    @DisplayName("Пустая перестановка — один беспорядок")
    @Test
    @Timeout(1)
    void countsEmptyDerangement() {
        assertThat("an empty permutation dont have exactly one derangement",
            Derangements.number(0), is(BigInteger.ONE));
    }

    @DisplayName("Отрицательный размер отвергается")
    @Test
    @Timeout(1)
    void cannotCountNegativeDerangements() {
        assertThrows(IllegalArgumentException.class, () -> Derangements.number(-1),
            "a negative derangement size is counted");
    }
}
