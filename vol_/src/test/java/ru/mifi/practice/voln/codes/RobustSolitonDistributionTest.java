package ru.mifi.practice.voln.codes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка устойчивого солитонного распределения степеней.
 *
 * <p>Точные вероятности здесь не проверяются: их смысл в том, что декодер сходится, а не в
 * конкретных числах. Проверяется то, на что опирается кодер, — степень всегда в пределах от
 * единицы до числа исходных символов, единица выпадает часто, и распределение повторяемо.
 */
@DisplayName("Устойчивое солитонное распределение")
final class RobustSolitonDistributionTest {

    @DisplayName("Степень не выходит за число исходных символов")
    @Test
    @Timeout(5)
    void staysWithinTheNumberOfSymbols() {
        RobustSolitonDistribution distribution = new RobustSolitonDistribution(20, 0.1d, 0.5d);
        DeterministicRandom random = new DeterministicRandom(20260804L);
        boolean outside = false;
        for (int step = 0; step < 10000; step++) {
            int degree = distribution.sampleDegree(random);
            outside |= degree < 1 || degree > 20;
        }
        assertThat("a degree escapes the number of source symbols", outside, is(false));
    }

    /** Без частых единиц декодеру не с чего начать: только степень один даёт готовый символ. */
    @DisplayName("Единичная степень выпадает заметно часто")
    @Test
    @Timeout(5)
    void yieldsDegreeOneOftenEnough() {
        RobustSolitonDistribution distribution = new RobustSolitonDistribution(20, 0.1d, 0.5d);
        DeterministicRandom random = new DeterministicRandom(20260804L);
        Map<Integer, Integer> counts = new HashMap<>();
        for (int step = 0; step < 10000; step++) {
            counts.merge(distribution.sampleDegree(random), 1, Integer::sum);
        }
        assertThat("degree one dont show up often enough", counts.getOrDefault(1, 0), is(greaterThan(500)));
    }

    @DisplayName("Одно зерно даёт одну последовательность степеней")
    @Test
    @Timeout(5)
    void repeatsTheDegreesForTheSameSeed() {
        RobustSolitonDistribution distribution = new RobustSolitonDistribution(20, 0.1d, 0.5d);
        assertThat("the same seed gives different degrees",
            distribution.sampleDegree(new DeterministicRandom(7L)),
            is(distribution.sampleDegree(new DeterministicRandom(7L))));
    }

    @DisplayName("Распределение из одного символа всегда даёт единицу")
    @Test
    @Timeout(5)
    void alwaysYieldsOneForASingleSymbol() {
        RobustSolitonDistribution distribution = new RobustSolitonDistribution(1, 0.1d, 0.5d);
        DeterministicRandom random = new DeterministicRandom(20260804L);
        boolean other = false;
        for (int step = 0; step < 1000; step++) {
            other |= distribution.sampleDegree(random) != 1;
        }
        assertThat("a single symbol distribution gives a degree other than one", other, is(false));
    }

    @DisplayName("Нулевое число символов отвергается")
    @Test
    @Timeout(1)
    void refusesAnEmptyAlphabet() {
        assertThrows(IllegalArgumentException.class, () -> new RobustSolitonDistribution(0, 0.1d, 0.5d),
            "an empty alphabet passes the check");
    }

    @DisplayName("Вероятность отказа вне полуинтервала отвергается")
    @Test
    @Timeout(1)
    void refusesAFailureProbabilityOutsideTheUnitInterval() {
        assertThrows(IllegalArgumentException.class, () -> new RobustSolitonDistribution(20, 0.1d, 1.0d),
            "a failure probability outside the unit interval passes the check");
    }
}
