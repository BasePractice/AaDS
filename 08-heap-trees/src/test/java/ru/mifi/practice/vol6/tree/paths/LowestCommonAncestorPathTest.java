package ru.mifi.practice.vol6.tree.paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.ParserText;
import ru.mifi.practice.vol6.tree.Tree;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/** Проверка пути между двумя значениями через наименьшего общего предка. */
@DisplayName("Путь через наименьшего общего предка")
final class LowestCommonAncestorPathTest {

    @DisplayName("Путь между братьями идёт через их родителя")
    @Test
    @Timeout(1)
    void goesThroughTheParentOfTwoSiblings() {
        assertThat("the path between siblings dont go through their parent",
            values(new LowestCommonAncestorPath<Integer>().path(tree(), 4, 5)), contains(4, 2, 5));
    }

    @DisplayName("Путь между ветвями идёт через корень")
    @Test
    @Timeout(1)
    void goesThroughTheRootBetweenBranches() {
        assertThat("the path between branches dont go through the root",
            values(new LowestCommonAncestorPath<Integer>().path(tree(), 4, 3)), contains(4, 2, 1, 3));
    }

    @DisplayName("Путь от предка к потомку спускается напрямую")
    @Test
    @Timeout(1)
    void descendsStraightFromAncestorToDescendant() {
        assertThat("the path from an ancestor to its descendant dont descend straight",
            values(new LowestCommonAncestorPath<Integer>().path(tree(), 1, 5)), contains(1, 2, 5));
    }

    @DisplayName("Путь из значения в себя состоит из одного узла")
    @Test
    @Timeout(1)
    void keepsASingleNodeForTheSameValue() {
        assertThat("the path from a value to itself holds more than one node",
            new LowestCommonAncestorPath<Integer>().path(tree(), 4, 4).size(), is(1));
    }

    private static List<Integer> values(List<Node<Integer>> path) {
        return path.stream().map(Node::value).toList();
    }

    private static Tree<Integer> tree() {
        try {
            return new ParserText<Integer>()
                .parse("1:{2,3}\n2:{4,5}\n", Integer::valueOf, Comparator.naturalOrder());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
