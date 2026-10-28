package ru.mifi.practice.boring.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.OptionalInt;
import java.util.Random;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка всех трёх упорядоченных множеств со штатным деревом на случайной череде операций.
 *
 * <p>Диапазон чисел узкий, поэтому повторные вставки и удаления отсутствующих чисел попадаются
 * часто: именно на них разъезжаются нити бора и этажи списка с пропусками.
 */
@DisplayName("Упорядоченные множества на случайных операциях")
final class OrderedRandomTest {

    @DisplayName("Декартово дерево отвечает как штатное множество")
    @Test
    @Timeout(10)
    void treapAgreesWithTheStandardSet() {
        assertThat("the treap disagrees with the standard set",
            disagreements(new Treap(new Random(20260815L))), is(0));
    }

    @DisplayName("Список с пропусками отвечает как штатное множество")
    @Test
    @Timeout(10)
    void skipListAgreesWithTheStandardSet() {
        assertThat("the skip list disagrees with the standard set",
            disagreements(new SkipList(new Random(20260815L))), is(0));
    }

    @DisplayName("X-быстрый бор отвечает как штатное множество")
    @Test
    @Timeout(10)
    void xFastTrieAgreesWithTheStandardSet() {
        assertThat("the x-fast trie disagrees with the standard set",
            disagreements(new XFastTrie()), is(0));
    }

    private static int disagreements(Ordered ordered) {
        Random random = new Random(20260815L);
        TreeSet<Integer> expected = new TreeSet<>();
        int mismatches = 0;
        for (int step = 0; step < 3000; step++) {
            int value = random.nextInt(200);
            if (random.nextBoolean()) {
                ordered.add(value);
                expected.add(value);
            } else {
                ordered.remove(value);
                expected.remove(value);
            }
            mismatches += ordered.size() == expected.size() ? 0 : 1;
            mismatches += ordered.contains(value) == expected.contains(value) ? 0 : 1;
            mismatches += ordered.next(value).equals(higher(expected, value)) ? 0 : 1;
            mismatches += ordered.previous(value).equals(lower(expected, value)) ? 0 : 1;
        }
        return mismatches;
    }

    private static OptionalInt higher(TreeSet<Integer> values, int value) {
        Integer higher = values.higher(value);
        return higher == null ? OptionalInt.empty() : OptionalInt.of(higher);
    }

    private static OptionalInt lower(TreeSet<Integer> values, int value) {
        Integer lower = values.lower(value);
        return lower == null ? OptionalInt.empty() : OptionalInt.of(lower);
    }
}
