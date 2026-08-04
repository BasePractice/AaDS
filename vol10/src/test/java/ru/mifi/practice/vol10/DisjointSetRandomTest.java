package ru.mifi.practice.vol10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка обеих реализаций непересекающихся множеств между собой на случайных объединениях.
 *
 * <p>Окрашивание — наивный эталон: оно перекрашивает всё множество и потому заведомо право.
 * Сжатие путей отвечает на те же вопросы дешевле, и на каждом шаге ответы обязаны совпадать.
 */
@DisplayName("Непересекающиеся множества на случайных объединениях")
final class DisjointSetRandomTest {

    @DisplayName("Сжатие путей отвечает так же, как окрашивание")
    @Test
    @Timeout(10)
    void agreesWithTheColoredReference() {
        Random random = new Random(20260804L);
        int size = 40;
        DisjointSet compressed = new DisjointSet.Compressed(size);
        DisjointSet colored = new DisjointSet.Colored(size);
        int mismatches = 0;
        for (int step = 0; step < 2000; step++) {
            int left = random.nextInt(size);
            int right = random.nextInt(size);
            if (random.nextBoolean()) {
                compressed.union(left, right, Counter.create());
                colored.union(left, right, Counter.create());
            } else if (compressed.connected(left, right, Counter.create())
                != colored.connected(left, right, Counter.create())) {
                ++mismatches;
            }
        }
        assertThat("path compression answers differently from the colored reference", mismatches, is(0));
    }

    @DisplayName("Число множеств у обеих реализаций совпадает")
    @Test
    @Timeout(10)
    void agreesOnTheNumberOfSets() {
        Random random = new Random(1L);
        int size = 40;
        DisjointSet compressed = new DisjointSet.Compressed(size);
        DisjointSet colored = new DisjointSet.Colored(size);
        for (int step = 0; step < 200; step++) {
            int left = random.nextInt(size);
            int right = random.nextInt(size);
            compressed.union(left, right, Counter.create());
            colored.union(left, right, Counter.create());
        }
        assertThat("the two implementations count sets differently",
            compressed.sets(), is(colored.sets()));
    }

    @DisplayName("Повторное объединение не уменьшает число множеств")
    @Test
    @Timeout(10)
    void keepsTheNumberOfSetsOnARepeatedUnion() {
        DisjointSet sets = new DisjointSet.Compressed(5);
        sets.union(0, 1, Counter.create());
        sets.union(0, 1, Counter.create());
        assertThat("a repeated union shrinks the number of sets", sets.sets(), is(4));
    }
}
