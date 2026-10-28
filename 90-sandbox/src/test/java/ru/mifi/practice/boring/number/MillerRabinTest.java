package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Тест простоты Миллера — Рабина")
final class MillerRabinTest {

    @DisplayName("Признаёт простое число простым")
    @Test
    @Timeout(1)
    void acceptsThePrime() {
        assertThat("a prime is reported as composite", new MillerRabin().prime(1_000_000_007L), is(true));
    }

    @DisplayName("Признаёт составное число составным")
    @Test
    @Timeout(1)
    void refusesTheComposite() {
        assertThat("a composite is reported as prime", new MillerRabin().prime(1_000_000_009L * 3), is(false));
    }

    @DisplayName("Не поддаётся числу Кармайкла, обманывающему проверку Ферма")
    @Test
    @Timeout(1)
    void refusesTheCarmichaelNumber() {
        assertThat("the carmichael number passes for a prime", new MillerRabin().prime(561), is(false));
    }

    @DisplayName("Единица и ноль простыми не считаются")
    @Test
    @Timeout(1)
    void refusesTheUnit() {
        assertThat("the unit passes for a prime", new MillerRabin().prime(1), is(false));
    }

    @DisplayName("Отвечает так же, как перебор делителей")
    @Test
    @Timeout(10)
    void agreesWithTrialDivision() {
        Primality fast = new MillerRabin();
        Primality slow = new TrialDivision();
        int mismatches = 0;
        for (long number = 0; number < 20_000; number++) {
            if (fast.prime(number) != slow.prime(number)) {
                ++mismatches;
            }
        }
        assertThat("the probabilistic test disagrees with trial division", mismatches, is(0));
    }

    @DisplayName("Отвечает на большом числе, где перебор делителей уже не годится")
    @Test
    @Timeout(1)
    void answersWhereTrialDivisionGivesUp() {
        assertThat("the large prime is not recognized",
            new MillerRabin().prime(2_305_843_009_213_693_951L), is(true));
    }
}
