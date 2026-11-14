package ru.mifi.practice.vol6.tree.search;

import java.util.List;

/** Повторяет вставки и поиск из лекции. */
public final class BTreeDemo {
    private BTreeDemo() {
    }

    public static void main(String[] args) {
        BTree tree = new BTree();
        for (int key : List.of(10, 20, 30, 40, 50, 60)) {
            tree.add(key);
            BTree.Shape root = tree.snapshot();
            System.out.printf("+%d  корень %s  потомки %s%n", key, root.keys(),
                    root.children().stream().map(BTree.Shape::keys).toList());
        }
        for (int key : List.of(30, 35, 40)) {
            BTree.Search result = tree.search(key);
            System.out.printf("%d  %s  посещено узлов: %d%n", key,
                    result.found() ? "найден" : "отсутствует", result.visitedNodes());
        }
    }
}
