package ru.mifi.practice.vol6.tree.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mifi.practice.vol6.Counter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

@DisplayName("AVL-дерево")
final class AVLTest {

    @DisplayName("Находит добавленное значение")
    @Test
    void findsAnAddedValue() {
        AVL<Integer> tree = new AVL<>();
        tree.add(5);
        assertThat("added value dont get found", tree.search(5, Counter.create()).isPresent(), is(true));
    }

    @DisplayName("Не находит отсутствующее значение")
    @Test
    void reportsMissingValue() {
        AVL<Integer> tree = new AVL<>();
        tree.add(5);
        assertThat("a missing value is reported as found", tree.search(6, Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Балансирует возрастающую последовательность")
    @Test
    void balancesAnAscendingSequence() {
        AVL<Integer> tree = new AVL<>();
        for (int i = 1; i <= 15; i++) {
            tree.add(i);
        }
        assertThat("an ascending sequence degrades the tree into a list", height(tree.root), lessThanOrEqualTo(4));
    }

    @DisplayName("Сохраняет баланс после каждого удаления в случайном порядке")
    @Test
    void keepsBalanceAfterEveryDelete() {
        Random random = new Random(42);
        int unbalanced = 0;
        for (int round = 0; round < 200; round++) {
            List<Integer> values = new ArrayList<>();
            for (int i = 1; i <= 40; i++) {
                values.add(i);
            }
            Collections.shuffle(values, random);
            AVL<Integer> tree = new AVL<>();
            for (Integer value : values) {
                tree.add(value);
            }
            Collections.shuffle(values, random);
            for (Integer value : values) {
                tree.delete(value);
                if (!balanced(tree.root)) {
                    ++unbalanced;
                }
            }
        }
        assertThat("delete leaves the tree unbalanced", unbalanced, is(0));
    }

    @DisplayName("Сохраняет порядок двоичного дерева поиска после удалений")
    @Test
    void keepsSearchOrderAfterDeletes() {
        AVL<Integer> tree = new AVL<>();
        for (int i = 1; i <= 20; i++) {
            tree.add(i);
        }
        for (int i = 1; i <= 10; i++) {
            tree.delete(i);
        }
        assertThat("delete breaks the search order", ordered(tree.root, 0, 21), is(true));
    }

    @DisplayName("Удалённое значение не находится")
    @Test
    void forgetsADeletedValue() {
        AVL<Integer> tree = new AVL<>();
        tree.add(5);
        tree.add(7);
        tree.delete(5);
        assertThat("a deleted value is still found", tree.search(5, Counter.create()).isPresent(), is(false));
    }

    private static int height(BinaryTree.Node<Integer> node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    private static boolean balanced(BinaryTree.Node<Integer> node) {
        if (node == null) {
            return true;
        }
        return Math.abs(height(node.left) - height(node.right)) <= 1
            && balanced(node.left) && balanced(node.right);
    }

    private static boolean ordered(BinaryTree.Node<Integer> node, int low, int high) {
        if (node == null) {
            return true;
        }
        return node.value > low && node.value < high
            && ordered(node.left, low, node.value) && ordered(node.right, node.value, high);
    }
}
