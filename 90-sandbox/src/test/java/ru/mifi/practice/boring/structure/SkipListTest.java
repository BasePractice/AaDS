package ru.mifi.practice.boring.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.OptionalInt;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Список с пропусками")
final class SkipListTest {

    @DisplayName("Хранит добавленное число")
    @Test
    @Timeout(1)
    void keepsTheAddedValue() {
        Ordered list = new SkipList(new Random(1L));
        list.add(42);
        assertThat("the added value is lost", list.contains(42), is(true));
    }

    @DisplayName("Повтор не увеличивает размер")
    @Test
    @Timeout(1)
    void refusesTheDuplicate() {
        Ordered list = new SkipList(new Random(1L));
        list.add(42);
        list.add(42);
        assertThat("the duplicate is stored twice", list.size(), is(1));
    }

    @DisplayName("Удалённое число больше не хранится")
    @Test
    @Timeout(1)
    void forgetsTheRemovedValue() {
        Ordered list = new SkipList(new Random(1L));
        list.add(42);
        list.remove(42);
        assertThat("the removed value is still found", list.contains(42), is(false));
    }

    @DisplayName("Удаление с верхних этажей не рвёт нижний")
    @Test
    @Timeout(1)
    void keepsTheGroundFloorAfterRemoval() {
        Ordered list = new SkipList(new Random(1L));
        for (int value : new int[]{10, 20, 30}) {
            list.add(value);
        }
        list.remove(20);
        assertThat("the ground floor lost a value after the removal", list.next(10), is(OptionalInt.of(30)));
    }

    @DisplayName("Следующее число — наименьшее из больших")
    @Test
    @Timeout(1)
    void findsTheSmallestOfTheLargerValues() {
        Ordered list = new SkipList(new Random(1L));
        for (int value : new int[]{10, 20, 30}) {
            list.add(value);
        }
        assertThat("the next value is wrong", list.next(15), is(OptionalInt.of(20)));
    }

    @DisplayName("Предыдущее число — наибольшее из меньших")
    @Test
    @Timeout(1)
    void findsTheLargestOfTheSmallerValues() {
        Ordered list = new SkipList(new Random(1L));
        for (int value : new int[]{10, 20, 30}) {
            list.add(value);
        }
        assertThat("the previous value is wrong", list.previous(25), is(OptionalInt.of(20)));
    }

    @DisplayName("Перед наименьшим числом ничего нет")
    @Test
    @Timeout(1)
    void findsNothingBeforeTheSmallest() {
        Ordered list = new SkipList(new Random(1L));
        list.add(10);
        assertThat("something is found before the smallest value",
            list.previous(10), is(OptionalInt.empty()));
    }

    @DisplayName("Пустой список ничего не находит")
    @Test
    @Timeout(1)
    void findsNothingWhenEmpty() {
        assertThat("the empty list finds a neighbour",
            new SkipList(new Random(1L)).next(0), is(OptionalInt.empty()));
    }
}
