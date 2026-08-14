package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Наименьший общий предок по Тарьяну")
final class TarjanLcaTest {

    @DisplayName("Предок вершин из разных поддеревьев — их развилка")
    @Test
    @Timeout(1)
    void findsTheForkOfTwoSubtrees() {
        TarjanLca.Query query = new TarjanLca.Query(3, 4);
        assertThat("the fork of the two subtrees is wrong",
            new TarjanLca().ancestors(tree(), 0, List.of(query)).get(query), is(1));
    }

    @DisplayName("Предок вершины и её потомка — она сама")
    @Test
    @Timeout(1)
    void findsTheAncestorOfItsOwnDescendant() {
        TarjanLca.Query query = new TarjanLca.Query(1, 4);
        assertThat("the ancestor of its own descendant is wrong",
            new TarjanLca().ancestors(tree(), 0, List.of(query)).get(query), is(1));
    }

    @DisplayName("Предок вершины и её самой — она сама")
    @Test
    @Timeout(1)
    void findsTheAncestorOfItself() {
        TarjanLca.Query query = new TarjanLca.Query(4, 4);
        assertThat("the ancestor of a vertex and itself is wrong",
            new TarjanLca().ancestors(tree(), 0, List.of(query)).get(query), is(4));
    }

    @DisplayName("Предок вершин из разных ветвей корня — корень")
    @Test
    @Timeout(1)
    void findsTheRootAsTheAncestor() {
        TarjanLca.Query query = new TarjanLca.Query(4, 5);
        assertThat("the root is not recognized as the ancestor",
            new TarjanLca().ancestors(tree(), 0, List.of(query)).get(query), is(0));
    }

    @DisplayName("Отвечает на все запросы за один обход")
    @Test
    @Timeout(1)
    void answersEveryQueryInASingleWalk() {
        List<TarjanLca.Query> queries = List.of(
            new TarjanLca.Query(3, 4), new TarjanLca.Query(4, 5), new TarjanLca.Query(2, 6));
        assertThat("a query is left unanswered",
            new TarjanLca().ancestors(tree(), 0, queries).size(), is(3));
    }

    /**
     * Дерево: 0 — корень, у него сыновья 1 и 2; у 1 — сыновья 3 и 4; у 2 — сыновья 5 и 6.
     */
    private static Graph tree() {
        return new Graph(7)
            .both(0, 1, 1).both(0, 2, 1)
            .both(1, 3, 1).both(1, 4, 1)
            .both(2, 5, 1).both(2, 6, 1);
    }
}
