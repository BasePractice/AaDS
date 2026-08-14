package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Разложение на множители ро-методом Полларда")
final class PollardRhoTest {

    @DisplayName("Раскладывает произведение двух простых")
    @Test
    @Timeout(5)
    void splitsTheProductOfTwoPrimes() {
        assertThat("the product of two primes is split wrong",
            new PollardRho().factors(91), is(List.of(7L, 13L)));
    }

    @DisplayName("Простое число раскладывается в себя")
    @Test
    @Timeout(5)
    void keepsThePrimeWhole() {
        assertThat("the prime is split into something", new PollardRho().factors(97), is(List.of(97L)));
    }

    @DisplayName("Повторяющиеся множители выписываются столько раз, сколько входят")
    @Test
    @Timeout(5)
    void repeatsTheRepeatedFactor() {
        assertThat("the repeated factor is written once",
            new PollardRho().factors(8), is(List.of(2L, 2L, 2L)));
    }

    @DisplayName("Произведение множителей равно самому числу")
    @Test
    @Timeout(10)
    void multipliesBackToTheNumber() {
        long number = 600_851_475_143L;
        long product = 1;
        for (long factor : new PollardRho().factors(number)) {
            product *= factor;
        }
        assertThat("the factors dont multiply back to the number", product, is(number));
    }

    @DisplayName("Раскладывает произведение двух больших простых")
    @Test
    @Timeout(10)
    void splitsTheProductOfTwoLargePrimes() {
        assertThat("the semiprime is split wrong",
            new PollardRho().factors(1_000_003L * 1_000_033L), is(List.of(1_000_003L, 1_000_033L)));
    }

    @DisplayName("Единицу раскладывать не на что")
    @Test
    @Timeout(1)
    void refusesTheUnit() {
        assertThat("the unit is factored silently",
            assertThrows(IllegalArgumentException.class, () -> new PollardRho().factors(1)).getMessage(),
            is("Разложению на множители подлежат числа от двух, а не 1"));
    }
}
