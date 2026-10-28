package ru.mifi.practice.vol8.automaton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@DisplayName("Разбиение состояний на неразличимые")
final class PartitionTest {

    @DisplayName("Держит допускающие состояния отдельно от остальных")
    @Test
    @Timeout(1)
    void keepsAcceptingApartFromRejecting() {
        assertThat("the accepting state shares its class with a rejecting one",
            new Partition(Set.of(0, 1), Set.of(1), Map.of(0, Map.of('a', 1), 1, Map.of('a', 1))).classes(),
            hasItem(Set.of(1)));
    }

    @DisplayName("Складывает неразличимые состояния в один класс")
    @Test
    @Timeout(1)
    void collectsIndistinguishableStates() {
        assertThat("the indistinguishable states end up in different classes",
            new Partition(Set.of(0, 1, 2), Set.of(1, 2),
                Map.of(0, Map.of('a', 1), 1, Map.of('a', 2), 2, Map.of('a', 2))).classes(),
            hasItem(Set.of(1, 2)));
    }

    @DisplayName("Разводит состояния, которые различает одно слово")
    @Test
    @Timeout(1)
    void tellsApartStatesSeparatedByAWord() {
        assertThat("the states separated by a single letter share a class",
            new Partition(Set.of(0, 1, 2), Set.of(2),
                Map.of(0, Map.of('a', 1), 1, Map.of('a', 2), 2, Map.of('a', 2))).classes(),
            hasSize(3));
    }
}
