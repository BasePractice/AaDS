package ru.mifi.practice.vol6.tree.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@DisplayName("Красно-чёрное дерево")
final class RBTTest {

    @DisplayName("Корень всегда чёрный")
    @Test
    @Timeout(1)
    void keepsTheRootBlack() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(15).add(25);
        assertThat("the root of a red-black tree is not black", tree.root.custom, is(1));
    }

    @DisplayName("У красного узла оба потомка чёрные")
    @Test
    @Timeout(1)
    void keepsRedNodesWithBlackChildren() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(15).add(25);
        assertThat("a red node has a red child", redChildrenAreBlack(tree.root), is(true));
    }

    @DisplayName("Чёрная высота одинакова на всех путях")
    @Test
    @Timeout(1)
    void keepsBlackHeightUniform() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(15).add(25);
        assertThat("the black height differs between paths", blackHeight(tree.root), greaterThanOrEqualTo(0));
    }

    @DisplayName("Сохраняет порядок двоичного дерева поиска")
    @Test
    @Timeout(1)
    void keepsSearchOrder() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(15).add(25);
        assertThat("nodes are out of search order",
            ordered(tree.root, Integer.MIN_VALUE, Integer.MAX_VALUE), is(true));
    }

    @DisplayName("Находит добавленное значение")
    @Test
    @Timeout(1)
    void findsAnAddedValue() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(15).add(25);
        assertThat("an added value is not found", tree.search(15, Counter.create()).isPresent(), is(true));
    }

    @DisplayName("Не находит отсутствующее значение")
    @Test
    @Timeout(1)
    void reportsMissingValue() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(15).add(25);
        assertThat("a missing value is reported as found", tree.search(100, Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Удалённое значение не находится")
    @Test
    @Timeout(1)
    void forgetsADeletedValue() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(40).add(50);
        tree.delete(20);
        assertThat("a deleted value is still found", tree.search(20, Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Удаление сохраняет остальные значения")
    @Test
    @Timeout(1)
    void keepsRemainingValuesAfterDelete() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(40).add(50);
        tree.delete(20);
        assertThat("delete removes an unrelated value", tree.search(30, Counter.create()).isPresent(), is(true));
    }

    @DisplayName("Удаление всех значений опустошает дерево")
    @Test
    @Timeout(1)
    void becomesEmptyWhenAllValuesAreDeleted() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(40).add(50);
        tree.delete(10);
        tree.delete(20);
        tree.delete(30);
        tree.delete(40);
        tree.delete(50);
        assertThat("the tree is not empty after deleting every value", tree.root, is(nullValue()));
    }

    @DisplayName("Чёрная высота остаётся одинаковой при росте дерева")
    @Test
    @Timeout(5)
    void keepsBlackHeightUniformWhileGrowing() {
        RBT<Integer> tree = new RBT<>();
        int minBlackHeight = 0;
        for (int i = 0; i < 100; i++) {
            tree.add(i);
            minBlackHeight = Math.min(minBlackHeight, blackHeight(tree.root));
        }
        assertThat("the black height differs between paths while the tree grows", minBlackHeight, greaterThanOrEqualTo(0));
    }

    @DisplayName("Чёрная высота остаётся одинаковой после удалений")
    @Test
    @Timeout(5)
    void keepsBlackHeightUniformAfterDeletions() {
        RBT<Integer> tree = new RBT<>();
        for (int i = 0; i < 100; i++) {
            tree.add(i);
        }
        int minBlackHeight = 0;
        for (int i = 0; i < 100; i += 2) {
            tree.delete(i);
            minBlackHeight = Math.min(minBlackHeight, blackHeight(tree.root));
        }
        assertThat("the black height differs between paths after deletions", minBlackHeight, greaterThanOrEqualTo(0));
    }

    @DisplayName("Оставшиеся значения находятся после удаления половины")
    @Test
    @Timeout(5)
    void findsRemainingValuesAfterDeletingEvens() {
        RBT<Integer> tree = new RBT<>();
        for (int i = 0; i < 100; i++) {
            tree.add(i);
        }
        for (int i = 0; i < 100; i += 2) {
            tree.delete(i);
        }
        boolean allFound = true;
        for (int i = 1; i < 100; i += 2) {
            allFound = allFound && tree.search(i, Counter.create()).isPresent();
        }
        assertThat("a remaining odd value is lost after deleting the evens", allFound, is(true));
    }

    @DisplayName("Удалённые значения не находятся после удаления половины")
    @Test
    @Timeout(5)
    void doesNotFindDeletedEvens() {
        RBT<Integer> tree = new RBT<>();
        for (int i = 0; i < 100; i++) {
            tree.add(i);
        }
        for (int i = 0; i < 100; i += 2) {
            tree.delete(i);
        }
        boolean anyFound = false;
        for (int i = 0; i < 100; i += 2) {
            anyFound = anyFound || tree.search(i, Counter.create()).isPresent();
        }
        assertThat("a deleted even value is still found", anyFound, is(false));
    }

    private static int blackOf(BinaryTree.Node<Integer> node) {
        return node == null ? 1 : node.custom;
    }

    private static boolean redChildrenAreBlack(BinaryTree.Node<Integer> node) {
        if (node == null) {
            return true;
        }
        if (node.custom == 0 && (blackOf(node.left) != 1 || blackOf(node.right) != 1)) {
            return false;
        }
        return redChildrenAreBlack(node.left) && redChildrenAreBlack(node.right);
    }

    private static int blackHeight(BinaryTree.Node<Integer> node) {
        if (node == null) {
            return 1;
        }
        int left = blackHeight(node.left);
        int right = blackHeight(node.right);
        if (left < 0 || right < 0 || left != right) {
            return -1;
        }
        return left + (node.custom == 1 ? 1 : 0);
    }

    private static boolean ordered(BinaryTree.Node<Integer> node, int low, int high) {
        if (node == null) {
            return true;
        }
        return node.value > low && node.value < high
            && ordered(node.left, low, node.value) && ordered(node.right, node.value, high);
    }
}
