package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка центроида: удаление найденного узла оставляет каждую часть не больше половины. */
@DisplayName("Центроид дерева")
final class CentroidTest {

    @DisplayName("Центроид перекошенного дерева смещён к тяжёлой ветви")
    @Test
    @Timeout(1)
    void shiftsTowardsTheHeavyBranch() {
        assertThat("the centroid dont shift towards the heavy branch",
            new Centroid<Integer>().centroid(tree("1:{2,3}\n2:{4,5}\n")).value(), is(2));
    }

    @DisplayName("Центроид сбалансированного дерева — его корень")
    @Test
    @Timeout(1)
    void takesTheRootOfABalancedTree() {
        assertThat("the centroid of a balanced tree is not its root",
            new Centroid<Integer>().centroid(tree("1:{2,3}\n2:{4,5}\n3:{6,7}\n")).value(), is(1));
    }

    @DisplayName("Центроид одинокого узла — он сам")
    @Test
    @Timeout(1)
    void takesTheOnlyNodeOfASingleNodeTree() {
        assertThat("the centroid of a single node tree is not that node",
            new Centroid<Integer>().centroid(tree("1:{,}\n")).value(), is(1));
    }

    @DisplayName("Центроид цепочки стоит в её середине")
    @Test
    @Timeout(1)
    void standsInTheMiddleOfAChain() {
        assertThat("the centroid of a chain dont stand in its middle",
            new Centroid<Integer>().centroid(tree("1:{2,}\n2:{3,}\n3:{4,}\n4:{5,}\n")).value(), is(3));
    }

    private static Tree<Integer> tree(String description) {
        try {
            return new ParserText<Integer>().parse(description, Integer::valueOf, Comparator.naturalOrder());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
