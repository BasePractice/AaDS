package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Куча с уменьшением ключа")
final class PrioritiesTest {

    @DisplayName("Первым отдаёт ключ наименьшего приоритета")
    @Test
    @Timeout(1)
    void givesTheKeyOfTheSmallestPriorityFirst() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>()
            .add("a", 5).add("b", 2).add("c", 9);
        assertThat("the smallest priority stays behind", priorities.poll(), is("b"));
    }

    @DisplayName("Уменьшение приоритета поднимает ключ к вершине")
    @Test
    @Timeout(1)
    void raisesTheKeyOnDecreasing() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>()
            .add("a", 5).add("b", 2).add("c", 9);
        priorities.decrease("c", 1);
        assertThat("the decreased key stays behind", priorities.poll(), is("c"));
    }

    @DisplayName("Отдаёт ключи по возрастанию приоритета")
    @Test
    @Timeout(1)
    void givesTheKeysInTheOrderOfPriority() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>()
            .add("a", 5).add("b", 2).add("c", 9).add("d", 7);
        priorities.decrease("d", 1);
        assertThat("the keys leave the heap out of order",
            priorities.poll() + priorities.poll() + priorities.poll() + priorities.poll(), is("dbac"));
    }

    @DisplayName("Знает, лежит ли ключ в куче")
    @Test
    @Timeout(1)
    void tellsWhetherItHoldsTheKey() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>().add("a", 5);
        priorities.poll();
        assertThat("the polled key is still reported as held", priorities.holds("a"), is(false));
    }

    @DisplayName("Отказывается уменьшать приоритет неизвестного ключа")
    @Test
    @Timeout(1)
    void refusesTheUnknownKey() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>().add("a", 5);
        assertThat("the unknown key is decreased silently",
            assertThrows(NoSuchElementException.class, () -> priorities.decrease("b", 1)).getMessage(),
            is("Ключа b в куче нет, уменьшать нечего"));
    }

    @DisplayName("Отказывается повышать приоритет вместо понижения")
    @Test
    @Timeout(1)
    void refusesToRaiseThePriority() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>().add("a", 5);
        assertThat("the priority is raised under the name of decreasing",
            assertThrows(IllegalArgumentException.class, () -> priorities.decrease("a", 7)).getMessage(),
            is("Приоритет ключа a только уменьшается, а 7 больше 5"));
    }

    @DisplayName("Отказывается отдавать ключ из пустой кучи")
    @Test
    @Timeout(1)
    void refusesToPollTheEmptyHeap() {
        assertThat("the empty heap gives away a key",
            assertThrows(NoSuchElementException.class, () -> new Priorities.Binary<String, Integer>().poll())
                .getMessage(),
            is("Куча пуста"));
    }

    @DisplayName("Отказывается класть ключ дважды")
    @Test
    @Timeout(1)
    void refusesTheDuplicateKey() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>().add("a", 5);
        assertThat("the duplicate key silently shadows the first one",
            assertThrows(IllegalArgumentException.class, () -> priorities.add("a", 1)).getMessage(),
            is("Ключ a уже лежит в куче"));
    }

    @DisplayName("Считает лежащие в ней ключи")
    @Test
    @Timeout(1)
    void countsTheKeysItHolds() {
        Priorities<String, Integer> priorities = new Priorities.Binary<String, Integer>().add("a", 5).add("b", 2);
        priorities.poll();
        assertThat("the size dont follow the polled key", priorities.size(), is(1));
    }
}
