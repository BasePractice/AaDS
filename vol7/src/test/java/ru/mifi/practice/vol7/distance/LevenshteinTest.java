package ru.mifi.practice.vol7.distance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThan;

@DisplayName("Расстояние Левенштейна")
final class LevenshteinTest {

    @DisplayName("Рекурсия оценивает пустой образец длиной второй строки")
    @Test
    @Timeout(1)
    void recursionMeasuresAnEmptySourceByTheTargetLength() {
        assertThat("recursion dont measure an empty source by the target length",
            new Levenshtein.LevenshteinRecursion().distance("", "bomb", Counter.create()), is(4));
    }

    @DisplayName("Рекурсия оценивает пустую цель длиной первой строки")
    @Test
    @Timeout(1)
    void recursionMeasuresAnEmptyTargetByTheSourceLength() {
        assertThat("recursion dont measure an empty target by the source length",
            new Levenshtein.LevenshteinRecursion().distance("boobs", "", Counter.create()), is(5));
    }

    @DisplayName("Рекурсия не находит расстояния между одинаковыми строками")
    @Test
    @Timeout(1)
    void recursionFindsNoDistanceBetweenEqualStrings() {
        assertThat("recursion finds a distance between equal strings",
            new Levenshtein.LevenshteinRecursion().distance("bomb", "bomb", Counter.create()), is(0));
    }

    @DisplayName("Рекурсия считает замену одного символа единицей")
    @Test
    @Timeout(1)
    void recursionCountsASingleSubstitutionAsOne() {
        assertThat("recursion dont count a single substitution as one",
            new Levenshtein.LevenshteinRecursion().distance("boat", "boot", Counter.create()), is(1));
    }

    @DisplayName("Рекурсия считает удаление и замену для boobs и bomb")
    @Test
    @Timeout(1)
    void recursionCountsTwoEditsForBoobsAndBomb() {
        assertThat("recursion dont count two edits for boobs and bomb",
            new Levenshtein.LevenshteinRecursion().distance("boobs", "bomb", Counter.create()), is(2));
    }

    @DisplayName("Рекурсия считает элементарные шаги")
    @Test
    @Timeout(1)
    void recursionCountsElementarySteps() {
        Counter counter = Counter.create();
        new Levenshtein.LevenshteinRecursion().distance("boobs", "bomb", counter);
        assertThat("recursion dont count elementary steps", counter.count(), is(greaterThan(0)));
    }

    @DisplayName("Динамика оценивает пустой образец длиной второй строки")
    @Test
    @Timeout(1)
    void dynamicMeasuresAnEmptySourceByTheTargetLength() {
        assertThat("dynamic table dont measure an empty source by the target length",
            new Levenshtein.VagnerFisherDynamited().distance("", "bomb", Counter.create()), is(4));
    }

    @DisplayName("Динамика оценивает пустую цель длиной первой строки")
    @Test
    @Timeout(1)
    void dynamicMeasuresAnEmptyTargetByTheSourceLength() {
        assertThat("dynamic table dont measure an empty target by the source length",
            new Levenshtein.VagnerFisherDynamited().distance("boobs", "", Counter.create()), is(5));
    }

    @DisplayName("Динамика не находит расстояния между одинаковыми строками")
    @Test
    @Timeout(1)
    void dynamicFindsNoDistanceBetweenEqualStrings() {
        assertThat("dynamic table finds a distance between equal strings",
            new Levenshtein.VagnerFisherDynamited().distance("bomb", "bomb", Counter.create()), is(0));
    }

    @DisplayName("Динамика считает замену одного символа единицей")
    @Test
    @Timeout(1)
    void dynamicCountsASingleSubstitutionAsOne() {
        assertThat("dynamic table dont count a single substitution as one",
            new Levenshtein.VagnerFisherDynamited().distance("boat", "boot", Counter.create()), is(1));
    }

    @DisplayName("Динамика справляется с длинной парой, недоступной рекурсии")
    @Test
    @Timeout(1)
    void dynamicHandlesALongPairBeyondRecursion() {
        assertThat("dynamic table dont handle a long pair",
            new Levenshtein.VagnerFisherDynamited().distance("kitten", "sitting", Counter.create()), is(3));
    }

    @DisplayName("Обе реализации дают одно расстояние")
    @Test
    @Timeout(1)
    void bothImplementationsAgreeOnTheDistance() {
        int recursion = new Levenshtein.LevenshteinRecursion().distance("boobs", "bomb", Counter.create());
        assertThat("implementations disagree on the distance",
            new Levenshtein.VagnerFisherDynamited().distance("boobs", "bomb", Counter.create()), is(recursion));
    }
}
