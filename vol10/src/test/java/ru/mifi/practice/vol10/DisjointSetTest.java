package ru.mifi.practice.vol10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Система непересекающихся множеств")
final class DisjointSetTest {

    @DisplayName("Свежий элемент лежит в собственном множестве")
    @Test
    @Timeout(1)
    void keepsEveryElementInItsOwnSet() {
        assertThat("a fresh element shares a set with a neighbour",
            new DisjointSet.Compressed(4).connected(0, 1, Counter.create()), is(false));
    }

    @DisplayName("После объединения элементы связаны")
    @Test
    @Timeout(1)
    void connectsElementsAfterUnion() {
        DisjointSet set = new DisjointSet.Compressed(4);
        set.union(0, 1, Counter.create());
        assertThat("union dont connect the elements", set.connected(0, 1, Counter.create()), is(true));
    }

    @DisplayName("Связность транзитивна")
    @Test
    @Timeout(1)
    void propagatesConnectivityTransitively() {
        DisjointSet set = new DisjointSet.Compressed(4);
        set.union(0, 1, Counter.create());
        set.union(1, 2, Counter.create());
        assertThat("connectivity dont propagate through the middle element",
            set.connected(0, 2, Counter.create()), is(true));
    }

    @DisplayName("Объединение уменьшает число множеств")
    @Test
    @Timeout(1)
    void reducesTheNumberOfSets() {
        DisjointSet set = new DisjointSet.Compressed(4);
        set.union(0, 1, Counter.create());
        assertThat("union dont reduce the number of sets", set.sets(), is(3));
    }

    @DisplayName("Повторное объединение ничего не меняет")
    @Test
    @Timeout(1)
    void ignoresRepeatedUnion() {
        DisjointSet set = new DisjointSet.Compressed(4);
        set.union(0, 1, Counter.create());
        set.union(0, 1, Counter.create());
        assertThat("a repeated union splits a set", set.sets(), is(3));
    }

    @DisplayName("Повторное объединение сообщает, что связь уже была")
    @Test
    @Timeout(1)
    void reportsThatElementsWereAlreadyTogether() {
        DisjointSet set = new DisjointSet.Compressed(4);
        set.union(0, 1, Counter.create());
        assertThat("a repeated union pretends to join two sets",
            set.union(1, 0, Counter.create()), is(false));
    }

    @DisplayName("Не связанные элементы остаются раздельными")
    @Test
    @Timeout(1)
    void keepsUnrelatedElementsApart() {
        DisjointSet set = new DisjointSet.Compressed(4);
        set.union(0, 1, Counter.create());
        set.union(2, 3, Counter.create());
        assertThat("unrelated sets leak into each other",
            set.connected(1, 2, Counter.create()), is(false));
    }

    @DisplayName("Сжатие путей делает вырожденную цепочку дешевле наивной перекраски")
    @Test
    @Timeout(5)
    void beatsTheNaiveColoringOnALongChain() {
        assertThat("path compression dont pay off against the naive coloring",
            steps(new DisjointSet.Compressed(1000)), lessThan(steps(new DisjointSet.Colored(1000))));
    }

    @DisplayName("Элемент вне множества использовать нельзя")
    @Test
    @Timeout(1)
    void cannotUseAnElementOutsideTheSet() {
        DisjointSet set = new DisjointSet.Compressed(4);
        assertThrows(IndexOutOfBoundsException.class, () -> set.find(4, Counter.create()),
            "an element outside the set is accepted");
    }

    @DisplayName("Множество отрицательного размера создать нельзя")
    @Test
    @Timeout(1)
    void cannotCreateASetOfNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new DisjointSet.Compressed(-1),
            "a set of negative size is created");
    }

    private static int steps(DisjointSet set) {
        Counter counter = Counter.create();
        for (int i = 1; i < 1000; i++) {
            set.union(i - 1, i, counter);
        }
        for (int i = 0; i < 1000; i++) {
            set.find(i, counter);
        }
        return counter.count();
    }
}
