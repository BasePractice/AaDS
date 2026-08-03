package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Комбинаторика")
final class CombinatoricsTest {

    @DisplayName("Число перестановок равно факториалу")
    @Test
    @Timeout(1)
    void countsPermutationsAsFactorial() {
        assertThat("permutation count dont match the factorial",
            Combinatorics.permutations(5), is(BigInteger.valueOf(120)));
    }

    @DisplayName("Число размещений учитывает порядок")
    @Test
    @Timeout(1)
    void countsArrangementsWithOrder() {
        assertThat("arrangement count dont account for order",
            Combinatorics.arrangements(5, 2), is(BigInteger.valueOf(20)));
    }

    @DisplayName("Число сочетаний не учитывает порядок")
    @Test
    @Timeout(1)
    void countsCombinationsWithoutOrder() {
        assertThat("combination count accounts for order",
            Combinatorics.combinations(5, 2), is(BigInteger.valueOf(10)));
    }

    @DisplayName("Выбор больше набора невозможен")
    @Test
    @Timeout(1)
    void countsNothingWhenTakingMoreThanAvailable() {
        assertThat("taking more elements than available is counted",
            Combinatorics.combinations(3, 5), is(BigInteger.ZERO));
    }

    @DisplayName("Факториал отрицательного числа не определён")
    @Test
    @Timeout(1)
    void cannotCountPermutationsOfNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> Combinatorics.permutations(-1),
            "a negative size is accepted");
    }

    @DisplayName("Порождает все перестановки")
    @Test
    @Timeout(1)
    void generatesEveryPermutation() {
        assertThat("the number of generated permutations dont match the factorial",
            Combinatorics.collect(Combinatorics.permutationsOf(4, Counter.create())).size(), is(24));
    }

    @DisplayName("Перестановки не повторяются")
    @Test
    @Timeout(1)
    void generatesDistinctPermutations() {
        List<String> generated = Combinatorics.collect(Combinatorics.permutationsOf(5, Counter.create()));
        assertThat("some permutations are generated twice", new HashSet<>(generated).size(), is(120));
    }

    @DisplayName("Перестановки идут в лексикографическом порядке")
    @Test
    @Timeout(1)
    void generatesPermutationsInLexicographicOrder() {
        assertThat("permutations dont come out in lexicographic order",
            Combinatorics.collect(Combinatorics.permutationsOf(3, Counter.create())),
            contains("[0, 1, 2]", "[0, 2, 1]", "[1, 0, 2]", "[1, 2, 0]", "[2, 0, 1]", "[2, 1, 0]"));
    }

    @DisplayName("Пустой набор даёт одну перестановку")
    @Test
    @Timeout(1)
    void generatesOnePermutationOfNothing() {
        assertThat("an empty set dont have exactly one permutation",
            Combinatorics.collect(Combinatorics.permutationsOf(0, Counter.create())).size(), is(1));
    }

    @DisplayName("Порождает все сочетания")
    @Test
    @Timeout(1)
    void generatesEveryCombination() {
        assertThat("the number of generated combinations dont match C(5,2)",
            Combinatorics.collect(Combinatorics.combinationsOf(5, 2, Counter.create())).size(), is(10));
    }

    @DisplayName("Сочетания идут в лексикографическом порядке")
    @Test
    @Timeout(1)
    void generatesCombinationsInLexicographicOrder() {
        assertThat("combinations dont come out in lexicographic order",
            Combinatorics.collect(Combinatorics.combinationsOf(4, 2, Counter.create())),
            contains("[0, 1]", "[0, 2]", "[0, 3]", "[1, 2]", "[1, 3]", "[2, 3]"));
    }

    @DisplayName("Выбор больше набора не порождает ничего")
    @Test
    @Timeout(1)
    void generatesNothingWhenTakingMoreThanAvailable() {
        assertThat("taking more elements than available produces tuples",
            Combinatorics.collect(Combinatorics.combinationsOf(3, 5, Counter.create())).size(), is(0));
    }
}
