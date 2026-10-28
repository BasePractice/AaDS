package ru.mifi.practice.vol6.tree.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка трёх деревьев поиска со штатным упорядоченным множеством на случайных вставках,
 * удалениях и запросах.
 *
 * <p>Диапазон значений узкий, чтобы повторные вставки и удаления отсутствующего попадались часто:
 * именно на них расходятся балансировки.
 */
@DisplayName("Деревья поиска на случайных входах")
final class BinaryTreeRandomTest {

    @DisplayName("Несбалансированное дерево отвечает как упорядоченное множество")
    @Test
    @Timeout(10)
    void unbalancedTreeAgreesWithTheStandardSet() {
        assertThat("the unbalanced tree answers differently from the standard set",
            disagreements(BinarySearchTree::new), is(List.of()));
    }

    @DisplayName("АВЛ-дерево отвечает как упорядоченное множество")
    @Test
    @Timeout(10)
    void avlTreeAgreesWithTheStandardSet() {
        assertThat("the AVL tree answers differently from the standard set",
            disagreements(AVL::new), is(List.of()));
    }

    @DisplayName("Красно-чёрное дерево отвечает как упорядоченное множество")
    @Test
    @Timeout(10)
    void redBlackTreeAgreesWithTheStandardSet() {
        assertThat("the red black tree answers differently from the standard set",
            disagreements(RBT::new), is(List.of()));
    }

    private static List<String> disagreements(java.util.function.Supplier<BinaryTree<Integer>> factory) {
        Random random = new Random(20260804L);
        List<String> mismatches = new ArrayList<>();
        for (int attempt = 0; attempt < 50; attempt++) {
            BinaryTree<Integer> tree = factory.get();
            Set<Integer> reference = new TreeSet<>();
            for (int step = 0; step < 60; step++) {
                int value = random.nextInt(30);
                if (random.nextInt(3) == 0) {
                    tree.delete(value);
                    reference.remove(value);
                } else {
                    tree.add(value);
                    reference.add(value);
                }
            }
            for (int value = 0; value < 30; value++) {
                boolean found = tree.search(value, Counter.create()).isPresent();
                if (found != reference.contains(value)) {
                    mismatches.add("значение " + value + ": дерево " + found + ", множество " + reference.contains(value));
                }
            }
        }
        return mismatches;
    }
}
