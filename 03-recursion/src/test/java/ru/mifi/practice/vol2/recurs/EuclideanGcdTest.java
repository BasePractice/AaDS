package ru.mifi.practice.vol2.recurs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Контрольные примеры к практике по алгоритму Евклида. */
@Disabled("Учебное задание: слайд 183")
@DisplayName("Алгоритм Евклида")
@Timeout(2)
final class EuclideanGcdTest {
    @ParameterizedTest
    @CsvSource({"252,105,21", "105,252,21", "17,13,1", "42,42,42", "42,0,42", "0,42,42", "0,0,0"})
    void recursionFindsTheGcd(long first, long second, long expected) {
        assertThat("recursion returns the wrong gcd", new EuclideanGcd().recursive(first, second, Counter.create()),
            is(expected));
    }

    @ParameterizedTest
    @CsvSource({"252,105,21", "105,252,21", "17,13,1", "42,42,42", "42,0,42", "0,42,42", "0,0,0"})
    void loopFindsTheGcd(long first, long second, long expected) {
        assertThat("the loop returns the wrong gcd", new EuclideanGcd().iterative(first, second, Counter.create()),
            is(expected));
    }

    @Test
    void recursionUsesThreeRemainders() {
        Counter counter = Counter.create();
        new EuclideanGcd().recursive(252, 105, counter);
        assertThat("recursion counts the wrong number of remainders", counter.count(), is(3));
    }

    @Test
    void loopUsesThreeRemainders() {
        Counter counter = Counter.create();
        new EuclideanGcd().iterative(252, 105, counter);
        assertThat("the loop counts the wrong number of remainders", counter.count(), is(3));
    }

    @ParameterizedTest
    @CsvSource({"-42,0", "0,-42"})
    void recursionRejectsNegativeArguments(long first, long second) {
        EuclideanGcd gcd = new EuclideanGcd();
        Counter counter = Counter.create();
        assertThrows(IllegalArgumentException.class, () -> gcd.recursive(first, second, counter));
    }

    @ParameterizedTest
    @CsvSource({"-42,0", "0,-42"})
    void loopRejectsNegativeArguments(long first, long second) {
        EuclideanGcd gcd = new EuclideanGcd();
        Counter counter = Counter.create();
        assertThrows(IllegalArgumentException.class, () -> gcd.iterative(first, second, counter));
    }
}
