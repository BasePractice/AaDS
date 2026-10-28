package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

/** Проверка диаметра: самый длинный путь между двумя узлами дерева. */
@DisplayName("Диаметр дерева")
final class DiameterTest {

    /** Путь ненаправлен, поэтому концы могут прийти в любом порядке — важен состав, не сторона. */
    @DisplayName("Диаметр цепочки — вся цепочка")
    @Test
    @Timeout(1)
    void takesTheWholeChain() {
        assertThat("the diameter of a chain is not the whole chain",
            values(new Diameter<Integer>().path(tree("1:{2, }\n2:{3, }\n3:{4, }\n"))),
            containsInAnyOrder(1, 2, 3, 4));
    }

    @DisplayName("Диаметр перекошенного дерева идёт от листа через корень к листу")
    @Test
    @Timeout(1)
    void spansFromLeafToLeafThroughTheRoot() {
        assertThat("the diameter dont span from leaf to leaf through the root",
            new Diameter<Integer>().path(tree("1:{2,3}\n2:{4,5}\n")).size(), is(4));
    }

    @DisplayName("Диаметр сбалансированного дерева охватывает четыре узла")
    @Test
    @Timeout(1)
    void spansFourNodesOfABalancedTree() {
        assertThat("the diameter of a balanced tree dont span four nodes",
            new Diameter<Integer>().path(tree("1:{2,3}\n2:{4,5}\n3:{6,7}\n")).size(), is(5));
    }

    private static List<Integer> values(List<Node<Integer>> path) {
        return path.stream().map(Node::value).toList();
    }

    private static Tree<Integer> tree(String description) {
        try {
            return new ParserText<Integer>().parse(description, Integer::valueOf, Comparator.naturalOrder());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
