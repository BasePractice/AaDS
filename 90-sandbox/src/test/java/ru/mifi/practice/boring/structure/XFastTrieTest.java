package ru.mifi.practice.boring.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.OptionalInt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("X-быстрый бор")
final class XFastTrieTest {

    @DisplayName("Хранит добавленное число")
    @Test
    @Timeout(1)
    void keepsTheAddedValue() {
        Ordered trie = new XFastTrie();
        trie.add(42);
        assertThat("the added value is lost", trie.contains(42), is(true));
    }

    @DisplayName("Повтор не увеличивает размер")
    @Test
    @Timeout(1)
    void refusesTheDuplicate() {
        Ordered trie = new XFastTrie();
        trie.add(42);
        trie.add(42);
        assertThat("the duplicate is stored twice", trie.size(), is(1));
    }

    @DisplayName("Удалённое число больше не хранится")
    @Test
    @Timeout(1)
    void forgetsTheRemovedValue() {
        Ordered trie = new XFastTrie();
        trie.add(42);
        trie.remove(42);
        assertThat("the removed value is still found", trie.contains(42), is(false));
    }

    @DisplayName("Следующее число — наименьшее из больших")
    @Test
    @Timeout(1)
    void findsTheSmallestOfTheLargerValues() {
        Ordered trie = new XFastTrie();
        for (int value : new int[]{10, 20, 30}) {
            trie.add(value);
        }
        assertThat("the next value is wrong", trie.next(15), is(OptionalInt.of(20)));
    }

    @DisplayName("Предыдущее число — наибольшее из меньших")
    @Test
    @Timeout(1)
    void findsTheLargestOfTheSmallerValues() {
        Ordered trie = new XFastTrie();
        for (int value : new int[]{10, 20, 30}) {
            trie.add(value);
        }
        assertThat("the previous value is wrong", trie.previous(25), is(OptionalInt.of(20)));
    }

    @DisplayName("Сосед находится и у числа, которое само лежит в боре")
    @Test
    @Timeout(1)
    void findsTheNeighbourOfAStoredValue() {
        Ordered trie = new XFastTrie();
        for (int value : new int[]{10, 20, 30}) {
            trie.add(value);
        }
        assertThat("the neighbour of a stored value is wrong", trie.next(20), is(OptionalInt.of(30)));
    }

    @DisplayName("Удаление соседа не рвёт цепочку листьев")
    @Test
    @Timeout(1)
    void keepsTheLeafChainAfterRemoval() {
        Ordered trie = new XFastTrie();
        for (int value : new int[]{10, 20, 30}) {
            trie.add(value);
        }
        trie.remove(20);
        assertThat("the leaf chain is broken by the removal", trie.next(10), is(OptionalInt.of(30)));
    }

    @DisplayName("Числа вне ширины бора отвергаются сразу")
    @Test
    @Timeout(1)
    void refusesTheValueOutOfWidth() {
        assertThat("a value out of the trie width is stored silently",
            assertThrows(IllegalArgumentException.class, () -> new XFastTrie().add(70_000)).getMessage(),
            is("Бор хранит числа от 0 до 65535, а получил 70000"));
    }
}
