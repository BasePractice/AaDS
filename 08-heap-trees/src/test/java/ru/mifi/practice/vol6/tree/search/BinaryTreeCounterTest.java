package ru.mifi.practice.vol6.tree.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Единая модель шага поиска и изоляция последовательных измерений. */
@DisplayName("Счётчики деревьев поиска")
@Timeout(5)
final class BinaryTreeCounterTest {
    @DisplayName("Пустая позиция тоже считается одним шагом поиска")
    @Test
    void countsTheFinalEmptyPosition() {
        for (BinaryTree<Integer> tree : emptyTrees()) {
            Counter counter = Counter.create();
            assertThat("an empty tree contains a key", tree.search(8, counter).isPresent(), is(false));
            assertThat("an empty search is not one step", counter.count(), is(1));
        }
    }

    @DisplayName("Одинаковые деревья дают одинаковые числа на смешанном вводе")
    @Test
    void comparesMixedInputsWithTheSameQueries() {
        for (BinaryTree<Integer> tree : emptyTrees()) {
            List.of(5, 2, 8, 1, 3, 6, 9, 4, 7).forEach(tree::add);
            assertSearches(tree, 2, 4);
        }
    }

    @DisplayName("Балансировка сокращает поиск на отсортированном вводе")
    @Test
    void comparesSortedInputsWithTheSameQueries() {
        BinaryTree<Integer> binary = new BinarySearchTree<>();
        BinaryTree<Integer> avl = new AVL<>();
        BinaryTree<Integer> redBlack = new RBT<>();
        for (BinaryTree<Integer> tree : List.of(binary, avl, redBlack)) {
            List.of(1, 2, 3, 4, 5, 6, 7, 8, 9).forEach(tree::add);
        }
        assertSearches(binary, 8, 10);
        assertSearches(avl, 3, 5);
        assertSearches(redBlack, 3, 5);
    }

    @DisplayName("Поиски накапливаются до явного сброса счётчика")
    @Test
    void accumulatesSearchStepsUntilReset() {
        for (BinaryTree<Integer> tree : emptyTrees()) {
            List.of(5, 2, 8, 1, 3, 6, 9, 4, 7).forEach(tree::add);
            Counter counter = Counter.create();
            tree.search(8, counter);
            tree.search(8, counter);
            assertThat("search resets the caller counter", counter.count(), is(4));
            counter.reset();
            tree.search(150, counter);
            assertThat("the reset does not isolate the missing query", counter.count(), is(4));
        }
    }

    @DisplayName("BST сохраняет множество и порядок после каждой операции")
    @Test
    void preservesBinaryTreeInvariants() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        verifyChanges(tree, () -> tree.root, "BST");
    }

    @DisplayName("AVL сохраняет множество, высоты и баланс после каждой операции")
    @Test
    void preservesAvlInvariants() {
        AVL<Integer> tree = new AVL<>();
        verifyChanges(tree, () -> tree.root, "AVL");
    }

    @DisplayName("RBT сохраняет множество, родителей, цвета и чёрную высоту")
    @Test
    void preservesRedBlackInvariants() {
        RBT<Integer> tree = new RBT<>();
        verifyChanges(tree, () -> tree.root, "RBT");
    }

    @DisplayName("Вставки учитывают нисходящие позиции и проходы балансировки")
    @Test
    void countsInsertionAndDuplicateSteps() {
        List<BinaryTree<Integer>> trees = emptyTrees();
        int[][] expected = {{1, 2, 3, 2}, {1, 3, 5, 1}, {2, 3, 5, 1}};
        List<Integer> keys = List.of(10, 5, 1, 5);
        for (int index = 0; index < trees.size(); index++) {
            Counter counter = Counter.create();
            for (int step = 0; step < keys.size(); step++) {
                counter.reset();
                assertThat("the counted insertion breaks chaining",
                    trees.get(index).add(keys.get(step), counter) == trees.get(index), is(true));
                assertThat("the insertion or duplicate counts wrong steps",
                    counter.count(), is(expected[index][step]));
            }
        }
    }

    @DisplayName("Удаление отсутствующего значения учитывает конечную пустую позицию")
    @Test
    void countsMissingDeletionSteps() {
        List<BinaryTree<Integer>> trees = emptyTrees();
        int[] expected = {2, 5, 3};
        for (int index = 0; index < trees.size(); index++) {
            BinaryTree<Integer> tree = trees.get(index);
            tree.add(10).add(5).add(1);
            Counter counter = Counter.create();
            tree.delete(99, counter);
            assertThat("the missing deletion counts wrong positions", counter.count(), is(expected[index]));
            assertThat("a missing deletion removes a key", tree.search(5, Counter.create()).isPresent(), is(true));
        }
    }

    @DisplayName("Поиск преемника и повторный обход удаления тоже учитываются")
    @Test
    void countsSuccessorTraversal() {
        List<BinaryTree<Integer>> trees = emptyTrees();
        int[] expected = {5, 7, 3};
        for (int index = 0; index < trees.size(); index++) {
            BinaryTree<Integer> tree = trees.get(index);
            tree.add(10).add(5).add(20).add(15);
            Counter counter = Counter.create();
            tree.delete(10, counter);
            assertThat("successor processing counts wrong steps", counter.count(), is(expected[index]));
            assertThat("the deleted root is still present", tree.search(10, Counter.create()).isPresent(), is(false));
            for (int value : List.of(5, 15, 20)) {
                assertThat("successor replacement loses a key", tree.search(value, Counter.create()).isPresent(), is(true));
            }
        }
    }

    @DisplayName("Удаление чёрного листа учитывает восстановление RBT")
    @Test
    void countsRedBlackDeletionRepair() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(5).add(15).add(20);
        Counter counter = Counter.create();
        tree.delete(5, counter);
        assertThat("red black deletion repair is omitted", counter.count(), is(4));
        assertState(tree.root, new TreeSet<>(List.of(10, 15, 20)), "RBT");
    }

    @DisplayName("Изменения накапливают шаги до reset, включая удаление корня и пустого дерева")
    @Test
    void accumulatesMutationStepsUntilReset() {
        List<BinaryTree<Integer>> trees = emptyTrees();
        int[] accumulated = {2, 2, 3};
        int[] deletion = {1, 1, 2};
        for (int index = 0; index < trees.size(); index++) {
            BinaryTree<Integer> tree = trees.get(index);
            Counter counter = Counter.create();
            tree.add(10, counter);
            tree.add(10, counter);
            assertThat("insertion resets the caller counter", counter.count(), is(accumulated[index]));
            counter.reset();
            tree.delete(10, counter);
            assertThat("deleting the only root counts wrong steps", counter.count(), is(deletion[index]));
            tree.delete(10, counter);
            assertThat("empty deletion resets or misses its position", counter.count(), is(deletion[index] + 1));
            counter.reset();
            tree.search(10, counter);
            assertThat("the explicit reset fails or the root remains", counter.count(), is(1));
        }
    }

    private static List<BinaryTree<Integer>> emptyTrees() {
        return List.of(new BinarySearchTree<>(), new AVL<>(), new RBT<>());
    }

    private static void assertSearches(BinaryTree<Integer> tree, int foundSteps, int missingSteps) {
        Counter counter = Counter.create();
        assertThat("an inserted key is missing", tree.search(8, counter).isPresent(), is(true));
        assertThat("the found query counts the wrong positions", counter.count(), is(foundSteps));
        counter.reset();
        assertThat("an absent key was found", tree.search(150, counter).isPresent(), is(false));
        assertThat("the missing query counts the wrong positions", counter.count(), is(missingSteps));
    }

    private static void verifyChanges(BinaryTree<Integer> tree, Supplier<BinaryTree.Node<Integer>> root, String kind) {
        Random random = new Random(20261128L);
        TreeSet<Integer> expected = new TreeSet<>();
        Counter counter = Counter.create();
        for (int step = 0; step < 200; step++) {
            int value = random.nextInt(25);
            if (random.nextBoolean()) {
                tree.add(value, counter);
                expected.add(value);
            } else {
                tree.delete(value, counter);
                expected.remove(value);
            }
            assertState(root.get(), expected, kind);
            assertThat("search disagrees after a change",
                tree.search(value, Counter.create()).isPresent(), is(expected.contains(value)));
        }
        for (int value : new ArrayList<>(expected)) {
            tree.delete(value, counter);
            expected.remove(value);
            assertState(root.get(), expected, kind);
        }
    }

    private static void assertState(BinaryTree.Node<Integer> root, TreeSet<Integer> expected, String kind) {
        if (root != null && "RBT".equals(kind)) {
            assertThat("the red black root is not black", root.custom, is(1));
        }
        Inspection result = inspect(root, null, Integer.MIN_VALUE, Integer.MAX_VALUE, kind);
        assertThat("the tree lost or duplicated a key", result.values(), is(new ArrayList<>(expected)));
    }

    private static Inspection inspect(BinaryTree.Node<Integer> node, BinaryTree.Node<Integer> parent,
                                       int low, int high, String kind) {
        if (node == null) {
            return new Inspection(-1, 1, List.of());
        }
        assertThat("the search ordering is broken", low < node.value && node.value < high, is(true));
        Inspection left = inspect(node.left, node, low, node.value, kind);
        Inspection right = inspect(node.right, node, node.value, high, kind);
        int height = 1 + Math.max(left.height(), right.height());
        if ("AVL".equals(kind)) {
            assertThat("the stored AVL height is stale", node.custom, is(height));
            assertThat("the AVL subtrees are unbalanced", Math.abs(left.height() - right.height()) <= 1, is(true));
        }
        if ("RBT".equals(kind)) {
            assertThat("the parent link is broken", node.parent == parent, is(true));
            assertThat("the node color is invalid", node.custom == 0 || node.custom == 1, is(true));
            assertThat("the black heights differ", left.blackHeight(), is(right.blackHeight()));
            if (node.custom == 0) {
                assertThat("the red node has a red left child", node.left == null || node.left.custom == 1, is(true));
                assertThat("the red node has a red right child", node.right == null || node.right.custom == 1, is(true));
            }
        }
        List<Integer> values = new ArrayList<>(left.values());
        values.add(node.value);
        values.addAll(right.values());
        return new Inspection(height, left.blackHeight() + (node.custom == 1 ? 1 : 0), values);
    }

    private record Inspection(int height, int blackHeight, List<Integer> values) {
    }
}
