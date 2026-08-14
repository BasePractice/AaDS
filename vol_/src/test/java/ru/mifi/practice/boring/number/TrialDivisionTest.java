package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Проверка простоты перебором делителей")
final class TrialDivisionTest {

    @DisplayName("Признаёт простое число простым")
    @Test
    @Timeout(1)
    void acceptsThePrime() {
        assertThat("a prime is reported as composite", new TrialDivision().prime(97), is(true));
    }

    @DisplayName("Признаёт составное число составным")
    @Test
    @Timeout(1)
    void refusesTheComposite() {
        assertThat("a composite is reported as prime", new TrialDivision().prime(91), is(false));
    }

    @DisplayName("Двойка простая")
    @Test
    @Timeout(1)
    void acceptsTwo() {
        assertThat("two is reported as composite", new TrialDivision().prime(2), is(true));
    }

    @DisplayName("Единица простой не считается")
    @Test
    @Timeout(1)
    void refusesTheUnit() {
        assertThat("the unit passes for a prime", new TrialDivision().prime(1), is(false));
    }

    @DisplayName("Квадрат простого составной")
    @Test
    @Timeout(1)
    void refusesTheSquareOfAPrime() {
        assertThat("the square of a prime passes for a prime", new TrialDivision().prime(49), is(false));
    }
}
