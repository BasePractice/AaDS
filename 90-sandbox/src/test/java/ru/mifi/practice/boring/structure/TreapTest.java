package ru.mifi.practice.boring.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.OptionalInt;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Декартово дерево")
final class TreapTest {

    @DisplayName("Хранит добавленное число")
    @Test
    @Timeout(1)
    void keepsTheAddedValue() {
        Ordered treap = new Treap(new Random(1L));
        treap.add(42);
        assertThat("the added value is lost", treap.contains(42), is(true));
    }

    @DisplayName("Не хранит того, чего не добавляли")
    @Test
    @Timeout(1)
    void keepsNothingElse() {
        Ordered treap = new Treap(new Random(1L));
        treap.add(42);
        assertThat("a value nobody added is found", treap.contains(41), is(false));
    }

    @DisplayName("Повтор не увеличивает размер")
    @Test
    @Timeout(1)
    void refusesTheDuplicate() {
        Ordered treap = new Treap(new Random(1L));
        treap.add(42);
        treap.add(42);
        assertThat("the duplicate is stored twice", treap.size(), is(1));
    }

    @DisplayName("Удалённое число больше не хранится")
    @Test
    @Timeout(1)
    void forgetsTheRemovedValue() {
        Ordered treap = new Treap(new Random(1L));
        treap.add(42);
        treap.remove(42);
        assertThat("the removed value is still found", treap.contains(42), is(false));
    }

    @DisplayName("Следующее число — наименьшее из больших")
    @Test
    @Timeout(1)
    void findsTheSmallestOfTheLargerValues() {
        Ordered treap = new Treap(new Random(1L));
        for (int value : new int[]{10, 20, 30}) {
            treap.add(value);
        }
        assertThat("the next value is wrong", treap.next(15), is(OptionalInt.of(20)));
    }

    @DisplayName("Предыдущее число — наибольшее из меньших")
    @Test
    @Timeout(1)
    void findsTheLargestOfTheSmallerValues() {
        Ordered treap = new Treap(new Random(1L));
        for (int value : new int[]{10, 20, 30}) {
            treap.add(value);
        }
        assertThat("the previous value is wrong", treap.previous(25), is(OptionalInt.of(20)));
    }

    @DisplayName("За наибольшим числом ничего нет")
    @Test
    @Timeout(1)
    void findsNothingBeyondTheLargest() {
        Ordered treap = new Treap(new Random(1L));
        treap.add(10);
        assertThat("something is found beyond the largest value", treap.next(10), is(OptionalInt.empty()));
    }

    @DisplayName("Дерево остаётся неглубоким на возрастающем входе")
    @Test
    @Timeout(5)
    void staysShallowOnTheIncreasingInput() {
        Ordered treap = new Treap(new Random(1L));
        for (int value = 0; value < 20_000; value++) {
            treap.add(value);
        }
        assertThat("the increasing input turned the treap into a list", treap.size(), is(20_000));
    }
}
