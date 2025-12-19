package ru.mifi.practice.vol6.tree.search;

import org.junit.jupiter.api.Test;
import ru.mifi.practice.vol6.Counter;
import org.junit.jupiter.api.Assertions;

public class RBTTest {
    @Test
    public void testAdd() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(15).add(25);

        Counter counter = Counter.create();
        Assertions.assertTrue(tree.search(10, counter).isPresent());
        Assertions.assertTrue(tree.search(20, counter).isPresent());
        Assertions.assertTrue(tree.search(30, counter).isPresent());
        Assertions.assertTrue(tree.search(15, counter).isPresent());
        Assertions.assertTrue(tree.search(25, counter).isPresent());
        Assertions.assertFalse(tree.search(100, counter).isPresent());
        verifyRBTProps(tree);
    }

    @Test
    public void testDelete() {
        RBT<Integer> tree = new RBT<>();
        tree.add(10).add(20).add(30).add(40).add(50);
        tree.delete(20);

        Counter counter = Counter.create();
        Assertions.assertTrue(tree.search(10, counter).isPresent());
        Assertions.assertFalse(tree.search(20, counter).isPresent());
        Assertions.assertTrue(tree.search(30, counter).isPresent());
        verifyRBTProps(tree);

        tree.delete(30);
        Assertions.assertFalse(tree.search(30, counter).isPresent());
        verifyRBTProps(tree);

        tree.delete(10);
        tree.delete(40);
        tree.delete(50);
        Assertions.assertNull(tree.root);
    }

    @Test
    public void testLargeTree() {
        RBT<Integer> tree = new RBT<>();
        for (int i = 0; i < 100; i++) {
            tree.add(i);
            verifyRBTProps(tree);
        }
        for (int i = 0; i < 100; i += 2) {
            tree.delete(i);
            verifyRBTProps(tree);
        }
        Counter counter = Counter.create();
        for (int i = 1; i < 100; i += 2) {
            Assertions.assertTrue(tree.search(i, counter).isPresent());
        }
        for (int i = 0; i < 100; i += 2) {
            Assertions.assertFalse(tree.search(i, counter).isPresent());
        }
    }

    private <T extends Comparable<T>> void verifyRBTProps(RBT<T> tree) {
        if (tree.root == null) {
            return;
        }
        // 1. Корень всегда черный (1)
        Assertions.assertEquals(1, tree.root.custom, "Root must be black");
        checkNode(tree.root);
        checkBlackHeight(tree.root);
    }

    private <T extends Comparable<T>> void checkNode(BinaryTree.Node<T> node) {
        if (node == null) {
            return;
        }
        // 2. Если узел красный, его дети должны быть черными
        if (node.custom == 0) { // RED
            if (node.left != null) {
                Assertions.assertEquals(1, node.left.custom, "Left child of red node must be black");
            }
            if (node.right != null) {
                Assertions.assertEquals(1, node.right.custom, "Right child of red node must be black");
            }
        }
        // Проверка BST свойства
        if (node.left != null) {
            Assertions.assertTrue(node.left.value.compareTo(node.value) < 0);
            checkNode(node.left);
        }
        if (node.right != null) {
            Assertions.assertTrue(node.right.value.compareTo(node.value) > 0);
            checkNode(node.right);
        }
    }

    private <T extends Comparable<T>> int checkBlackHeight(BinaryTree.Node<T> node) {
        if (node == null) {
            return 1;
        }
        int leftHeight = checkBlackHeight(node.left);
        int rightHeight = checkBlackHeight(node.right);
        Assertions.assertEquals(leftHeight, rightHeight, "Black height mismatch at node " + node.value);
        return leftHeight + (node.custom == 1 ? 1 : 0);
    }
}
