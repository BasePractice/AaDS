package ru.mifi.practice.vol6.tree.paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.ParserText;
import ru.mifi.practice.vol6.tree.Tree;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Проверка пути, полученного слиянием путей до корня.
 *
 * <p>Ответ обязан совпадать с путём через наименьшего общего предка: способ поиска другой, а
 * путь между двумя узлами дерева единственный.
 */
@DisplayName("Путь слиянием путей до корня")
final class MergePathTest {

    @DisplayName("Путь между братьями идёт через их родителя")
    @Test
    @Timeout(1)
    void goesThroughTheParentOfTwoSiblings() {
        assertThat("the path between siblings dont go through their parent",
            values(new MergePath<Integer>().path(tree(), 4, 5)), contains(4, 2, 5));
    }

    @DisplayName("Путь между ветвями идёт через корень")
    @Test
    @Timeout(1)
    void goesThroughTheRootBetweenBranches() {
        assertThat("the path between branches dont go through the root",
            values(new MergePath<Integer>().path(tree(), 4, 3)), contains(4, 2, 1, 3));
    }

    @DisplayName("Путь от предка к потомку спускается напрямую")
    @Test
    @Timeout(1)
    void descendsStraightFromAncestorToDescendant() {
        assertThat("the path from an ancestor to its descendant dont descend straight",
            values(new MergePath<Integer>().path(tree(), 1, 5)), contains(1, 2, 5));
    }

    @DisplayName("Слияние путей даёт то же, что и наименьший общий предок")
    @Test
    @Timeout(1)
    void agreesWithTheLowestCommonAncestor() {
        assertThat("merging paths disagrees with the lowest common ancestor",
            values(new MergePath<Integer>().path(tree(), 4, 3)),
            is(values(new LowestCommonAncestorPath<Integer>().path(tree(), 4, 3))));
    }

    private static List<Integer> values(List<Node<Integer>> path) {
        List<Integer> values = new ArrayList<>();
        path.forEach(node -> values.add(node.value()));
        return values;
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
