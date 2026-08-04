package ru.mifi.practice.vol5.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка префиксного дерева путей: точные сегменты, подстановки и промахи. */
@DisplayName("Префиксное дерево путей")
final class PathTreeTest {

    @DisplayName("Точный путь находит своё значение")
    @Test
    @Timeout(1)
    void matchesAnExactPath() {
        PathTree<String, Integer> tree = PathTree.create();
        tree.add(new String[]{"api", "orders"}, 1);
        assertThat("an exact path dont find its value",
            tree.match(new String[]{"api", "orders"}).orElseThrow(), is(1));
    }

    @DisplayName("Подстановка в середине пути принимает любой сегмент")
    @Test
    @Timeout(1)
    void matchesAWildcardInTheMiddle() {
        PathTree<String, Integer> tree = PathTree.create();
        tree.add(new String[]{"api", "{id}", "items"}, 2);
        assertThat("a wildcard in the middle dont accept any segment",
            tree.match(new String[]{"api", "7", "items"}).orElseThrow(), is(2));
    }

    @DisplayName("Подстановка в начале пути принимает любой сегмент")
    @Test
    @Timeout(1)
    void matchesAWildcardAtTheStart() {
        PathTree<String, Integer> tree = PathTree.create();
        tree.add(new String[]{"{id}", "items"}, 3);
        assertThat("a wildcard at the start dont accept any segment",
            tree.match(new String[]{"7", "items"}).orElseThrow(), is(3));
    }

    @DisplayName("Путь из одного сегмента находит своё значение")
    @Test
    @Timeout(1)
    void matchesASingleSegmentPath() {
        PathTree<String, Integer> tree = PathTree.create();
        tree.add(new String[]{"api"}, 4);
        assertThat("a single segment path dont find its value",
            tree.match(new String[]{"api"}).orElseThrow(), is(4));
    }

    @DisplayName("Точный сегмент выигрывает у подстановки")
    @Test
    @Timeout(1)
    void prefersTheExactSegmentOverTheWildcard() {
        PathTree<String, Integer> tree = PathTree.create();
        tree.add(new String[]{"api", "{id}", "items"}, 5);
        tree.add(new String[]{"api", "me", "items"}, 6);
        assertThat("the wildcard wins over the exact segment",
            tree.match(new String[]{"api", "me", "items"}).orElseThrow(), is(6));
    }

    @DisplayName("Чужой путь ничего не находит")
    @Test
    @Timeout(1)
    void findsNothingForAForeignPath() {
        PathTree<String, Integer> tree = PathTree.create();
        tree.add(new String[]{"api", "orders"}, 7);
        assertThat("a foreign path finds a value",
            tree.match(new String[]{"web", "orders"}).isPresent(), is(false));
    }

    @DisplayName("Незавершённый путь ничего не находит")
    @Test
    @Timeout(1)
    void findsNothingForAnUnfinishedPath() {
        PathTree<String, Integer> tree = PathTree.create();
        tree.add(new String[]{"api", "orders", "items"}, 8);
        assertThat("an unfinished path finds a value",
            tree.match(new String[]{"api", "orders"}).isPresent(), is(false));
    }
}
