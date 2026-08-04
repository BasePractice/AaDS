package ru.mifi.practice.voln.codes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка детерминированного генератора SplitMix64.
 *
 * <p>Главное свойство здесь не случайность, а повторяемость: кодер и декодер порождают одну и ту
 * же последовательность из одного зерна, иначе восстановить данные нечем.
 */
@DisplayName("Детерминированный генератор")
final class DeterministicRandomTest {

    @DisplayName("Одно зерно даёт одну последовательность")
    @Test
    @Timeout(1)
    void repeatsTheSequenceForTheSameSeed() {
        assertThat("the same seed gives different sequences",
            sequence(20260804L), is(sequence(20260804L)));
    }

    @DisplayName("Разные зёрна дают разные последовательности")
    @Test
    @Timeout(1)
    void variesTheSequenceForDifferentSeeds() {
        assertThat("different seeds give the same sequence",
            sequence(1L).equals(sequence(2L)), is(false));
    }

    @DisplayName("Числа в диапазоне не выходят за границу")
    @Test
    @Timeout(1)
    void staysWithinTheBound() {
        DeterministicRandom random = new DeterministicRandom(20260804L);
        boolean outside = false;
        for (int step = 0; step < 10000; step++) {
            int value = random.nextInt(10);
            outside |= value < 0 || value >= 10;
        }
        assertThat("a value escapes the bound", outside, is(false));
    }

    @DisplayName("Диапазон покрывается целиком")
    @Test
    @Timeout(1)
    void coversTheWholeBound() {
        DeterministicRandom random = new DeterministicRandom(20260804L);
        Set<Integer> seen = new HashSet<>();
        for (int step = 0; step < 10000; step++) {
            seen.add(random.nextInt(10));
        }
        assertThat("the generator dont cover the whole bound", seen.size(), is(10));
    }

    @DisplayName("Вещественные числа лежат в полуинтервале от нуля до единицы")
    @Test
    @Timeout(1)
    void keepsDoublesBelowOne() {
        DeterministicRandom random = new DeterministicRandom(20260804L);
        boolean outside = false;
        for (int step = 0; step < 10000; step++) {
            double value = random.nextDouble();
            outside |= value < 0.0 || value >= 1.0;
        }
        assertThat("a double escapes the unit interval", outside, is(false));
    }

    @DisplayName("Последовательность не стоит на месте")
    @Test
    @Timeout(1)
    void movesFromStepToStep() {
        DeterministicRandom random = new DeterministicRandom(20260804L);
        Set<Long> seen = new HashSet<>();
        for (int step = 0; step < 1000; step++) {
            seen.add(random.nextLong());
        }
        assertThat("the sequence stands still", seen.size(), is(greaterThan(990)));
    }

    @DisplayName("Неположительная граница отвергается")
    @Test
    @Timeout(1)
    void refusesANonPositiveBound() {
        assertThrows(IllegalArgumentException.class, () -> new DeterministicRandom(1L).nextInt(0),
            "a non positive bound passes the check");
    }

    private static List<Long> sequence(long seed) {
        DeterministicRandom random = new DeterministicRandom(seed);
        List<Long> values = new ArrayList<>();
        for (int step = 0; step < 20; step++) {
            values.add(random.nextLong());
        }
        return values;
    }
}
