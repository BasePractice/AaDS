package ru.mifi.practice.vol7.backpack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Рюкзак")
final class BackpackTest {

    @DisplayName("Предмет весом ровно во вместимость помещается")
    @Test
    @Timeout(1)
    void takesAnItemWeighingExactlyTheCapacity() {
        assertThat("an item weighing exactly the capacity dont fit",
            new Backpack.Classic(1).putting(List.of(new Backpack.Item("гвоздь", 1, 10))),
            contains(new Backpack.Item("гвоздь", 1, 10)));
    }

    @DisplayName("Слишком тяжёлый предмет не берётся")
    @Test
    @Timeout(1)
    void skipsAnItemHeavierThanTheCapacity() {
        assertThat("an item heavier than the capacity is taken anyway",
            new Backpack.Classic(1).putting(List.of(new Backpack.Item("молот", 10, 100))).size(), is(0));
    }

    @DisplayName("Выбирает более ценный набор")
    @Test
    @Timeout(1)
    void picksTheMoreValuableSet() {
        assertThat("the cheaper set wins",
            new Backpack.Classic(5).putting(List.of(
                new Backpack.Item("камень", 5, 1),
                new Backpack.Item("слиток", 5, 100))),
            contains(new Backpack.Item("слиток", 5, 100)));
    }

    @DisplayName("Набирает несколько предметов до вместимости")
    @Test
    @Timeout(1)
    void combinesSeveralItemsUpToTheCapacity() {
        assertThat("the solver dont combine items up to the capacity",
            new Backpack.Classic(3).putting(List.of(
                new Backpack.Item("а", 1, 10),
                new Backpack.Item("б", 2, 20))).size(), is(2));
    }

    @DisplayName("Пустой список предметов даёт пустой рюкзак")
    @Test
    @Timeout(1)
    void putsNothingWhenThereIsNothingToPut() {
        assertThat("an empty item list dont produce an empty backpack",
            new Backpack.Classic(5).putting(List.of()).size(), is(0));
    }

    @Test
    @DisplayName("Порог ценности проверяется по оптимальному набору")
    void checksThreshold() {
        List<Backpack.Item> items = List.of(
            new Backpack.Item("A", 2, 3), new Backpack.Item("B", 3, 4), new Backpack.Item("C", 4, 5));
        List<Backpack.Item> selected = new Backpack.Classic(5).putting(items);
        int value = selected.stream().mapToInt(Backpack.Item::needed).sum();
        assertThat("вес набора", selected.stream().mapToInt(Backpack.Item::weight).sum(), is(5));
        assertThat("ценность набора", value, is(7));
        assertThat("порог 7", value >= 7, is(true));
        assertThat("порог 8", value >= 8, is(false));
    }

    @Test
    @DisplayName("Рюкзак проверяет достижимость точной суммы")
    void reducesSubsetSum() {
        List<Backpack.Item> items = List.of(
            new Backpack.Item("2", 2, 2), new Backpack.Item("3", 3, 3), new Backpack.Item("7", 7, 7));
        for (int target : List.of(5, 6)) {
            List<Backpack.Item> selected = new Backpack.Classic(target).putting(items);
            int sum = selected.stream().mapToInt(Backpack.Item::needed).sum();
            assertThat("лучшая достижимая сумма", sum, is(5));
            assertThat("достижимость цели " + target, sum >= target, is(target == 5));
        }
    }

    @Test
    @DisplayName("Каждый новый бит ёмкости удваивает число посещённых ячеек")
    void countsVisitedCells() {
        List<Backpack.Item> items = List.of(
            new Backpack.Item("A", 2, 3), new Backpack.Item("B", 3, 4), new Backpack.Item("C", 4, 5));
        for (int bits = 4; bits <= 7; bits++) {
            int capacity = (1 << bits) - 1;
            AtomicInteger visits = new AtomicInteger();
            List<Backpack.Item> selected = new Backpack.Classic(capacity).putting(items, visits::incrementAndGet);
            int value = selected.stream().mapToInt(Backpack.Item::needed).sum();
            assertThat("число битов ёмкости", Integer.toBinaryString(capacity).length(), is(bits));
            assertThat("посещения при W=" + capacity, visits.get(), is(4 * (1 << bits)));
            assertThat("ценность при W=" + capacity, value, is(12));
        }
    }
}
