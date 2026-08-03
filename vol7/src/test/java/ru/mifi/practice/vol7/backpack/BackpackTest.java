package ru.mifi.practice.vol7.backpack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Рюкзак")
final class BackpackTest {

    @DisplayName("Предмет весом ровно во вместимость помещается")
    @Test
    void takesAnItemWeighingExactlyTheCapacity() {
        assertThat("an item weighing exactly the capacity dont fit",
            new Backpack.Classic(1).putting(List.of(new Backpack.Item("гвоздь", 1, 10))),
            contains(new Backpack.Item("гвоздь", 1, 10)));
    }

    @DisplayName("Слишком тяжёлый предмет не берётся")
    @Test
    void skipsAnItemHeavierThanTheCapacity() {
        assertThat("an item heavier than the capacity is taken anyway",
            new Backpack.Classic(1).putting(List.of(new Backpack.Item("молот", 10, 100))).size(), is(0));
    }

    @DisplayName("Выбирает более ценный набор")
    @Test
    void picksTheMoreValuableSet() {
        assertThat("the cheaper set wins",
            new Backpack.Classic(5).putting(List.of(
                new Backpack.Item("камень", 5, 1),
                new Backpack.Item("слиток", 5, 100))),
            contains(new Backpack.Item("слиток", 5, 100)));
    }

    @DisplayName("Набирает несколько предметов до вместимости")
    @Test
    void combinesSeveralItemsUpToTheCapacity() {
        assertThat("the solver dont combine items up to the capacity",
            new Backpack.Classic(3).putting(List.of(
                new Backpack.Item("а", 1, 10),
                new Backpack.Item("б", 2, 20))).size(), is(2));
    }

    @DisplayName("Пустой список предметов даёт пустой рюкзак")
    @Test
    void putsNothingWhenThereIsNothingToPut() {
        assertThat("an empty item list dont produce an empty backpack",
            new Backpack.Classic(5).putting(List.of()).size(), is(0));
    }
}
