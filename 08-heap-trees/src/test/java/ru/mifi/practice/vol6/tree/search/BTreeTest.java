package ru.mifi.practice.vol6.tree.search;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BTreeTest {
    @Test
    void followsLectureExample() {
        BTree tree = new BTree();
        assertEquals(new BTree.Search(false, 0), tree.search(30));
        for (int key = 10; key <= 60; key += 10) {
            assertTrue(tree.add(key));
            assertValid(tree.snapshot(), Long.MIN_VALUE, Long.MAX_VALUE, true);
        }
        BTree.Shape root = tree.snapshot();
        assertEquals(List.of(20, 40), root.keys());
        assertEquals(List.of(List.of(10), List.of(30), List.of(50, 60)),
                root.children().stream().map(BTree.Shape::keys).toList());
        assertEquals(new BTree.Search(true, 2), tree.search(30));
        assertEquals(new BTree.Search(false, 2), tree.search(35));
        assertEquals(new BTree.Search(true, 1), tree.search(40));
        assertFalse(tree.add(40));
        assertEquals(root, tree.snapshot());
    }

    @Test
    void splitsInternalChild() {
        BTree tree = new BTree();
        for (int key = 1; key <= 17; key++) {
            assertTrue(tree.add(key));
            assertValid(tree.snapshot(), Long.MIN_VALUE, Long.MAX_VALUE, true);
        }
        assertEquals(List.of(4, 8, 12), tree.snapshot().keys());
        assertEquals(List.of(2, 6, 10, 14), tree.snapshot().children().stream()
                .map(node -> node.keys().get(0)).toList());
    }

    @Test
    void agreesWithSortedSet() {
        BTree tree = new BTree();
        TreeSet<Integer> expected = new TreeSet<>();
        Random random = new Random(12);
        for (int index = 0; index < 500; index++) {
            int key = random.nextInt(401) - 200;
            assertEquals(expected.add(key), tree.add(key));
            assertValid(tree.snapshot(), Long.MIN_VALUE, Long.MAX_VALUE, true);
        }
        assertTrue(tree.add(Integer.MIN_VALUE));
        assertTrue(tree.add(Integer.MAX_VALUE));
        expected.add(Integer.MIN_VALUE);
        expected.add(Integer.MAX_VALUE);
        assertEquals(new ArrayList<>(expected),
                assertValid(tree.snapshot(), Long.MIN_VALUE, Long.MAX_VALUE, true).keys());
        for (int key = -201; key <= 201; key++) {
            assertEquals(expected.contains(key), tree.search(key).found());
        }
    }

    private static Checked assertValid(BTree.Shape node, long lower, long upper, boolean root) {
        List<Integer> keys = node.keys();
        assertTrue(keys.size() <= 3);
        assertTrue(root && node.children().isEmpty() || !keys.isEmpty());
        long previous = lower;
        for (int key : keys) {
            assertTrue(previous < key && key < upper);
            previous = key;
        }
        if (node.children().isEmpty()) {
            return new Checked(0, keys);
        }
        assertEquals(keys.size() + 1, node.children().size());
        List<Integer> ordered = new ArrayList<>();
        int depth = -1;
        long from = lower;
        for (int index = 0; index < node.children().size(); index++) {
            long to = index < keys.size() ? keys.get(index) : upper;
            Checked child = assertValid(node.children().get(index), from, to, false);
            if (depth >= 0) {
                assertEquals(depth, child.depth());
            }
            depth = child.depth();
            ordered.addAll(child.keys());
            if (index < keys.size()) {
                ordered.add(keys.get(index));
            }
            from = to;
        }
        return new Checked(depth + 1, ordered);
    }

    private record Checked(int depth, List<Integer> keys) {
    }
}
