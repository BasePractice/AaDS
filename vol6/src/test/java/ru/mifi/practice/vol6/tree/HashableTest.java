package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Проверка структурного хеша дерева.
 *
 * <p>От хеша требуется двустороннее: одинаковые деревья обязаны совпадать по нему всегда, а
 * разные — расходиться хотя бы там, где различие видно невооружённым глазом. Первое — контракт,
 * второе — смысл: хеш, одинаковый у всех деревьев, честно выполняет контракт и ничего не даёт.
 */
@DisplayName("Структурный хеш дерева")
final class HashableTest {

    @DisplayName("Одинаковые деревья дают одинаковый хеш")
    @Test
    @Timeout(1)
    void agreesOnEqualTrees() {
        assertThat("equal trees give different hashes",
            tree("1:{2,3}\n2:{4,5}\n").hash(), is(tree("1:{2,3}\n2:{4,5}\n").hash()));
    }

    @DisplayName("Пустое дерево даёт нулевой хеш")
    @Test
    @Timeout(1)
    void takesZeroForAnEmptyTree() {
        assertThat("an empty tree gives a non zero hash", tree("").hash(), is(0));
    }

    @DisplayName("Разные значения в корне дают разный хеш")
    @Test
    @Timeout(1)
    void separatesTreesByTheRootValue() {
        assertThat("trees with different roots give the same hash",
            tree("1:{2,3}\n").hash(), is(not(tree("9:{2,3}\n").hash())));
    }

    @DisplayName("Разные значения в потомках дают разный хеш")
    @Test
    @Timeout(1)
    void separatesTreesByTheChildValues() {
        assertThat("trees with different children give the same hash",
            tree("1:{2,3}\n").hash(), is(not(tree("1:{4,5}\n").hash())));
    }

    @DisplayName("Разная форма при тех же значениях даёт разный хеш")
    @Test
    @Timeout(1)
    void separatesTreesByTheirShape() {
        assertThat("trees of different shape give the same hash",
            tree("1:{2,3}\n2:{4,}\n").hash(), is(not(tree("1:{2,3}\n3:{4,}\n").hash())));
    }

    private static Tree<Integer> tree(String description) {
        try {
            return new ParserText<Integer>().parse(description, Integer::valueOf, Comparator.naturalOrder());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
