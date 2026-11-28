package ru.mifi.practice.vol6.tree.search;

import ru.mifi.practice.commons.Counter;

import java.util.List;

/** Сравнение одного поиска в трёх деревьях на одинаковых входных данных. */
public abstract class Main {
    public static void main(String[] args) {
        compare("Смешанный порядок", List.of(5, 2, 8, 1, 3, 6, 9, 4, 7));
        compare("Отсортированный порядок", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    private static void compare(String title, List<Integer> values) {
        System.out.println(title + ": " + values);
        System.out.println("Шаг поиска — посещение узла, включая конечный NIL при отсутствии ключа.");
        System.out.println("Дерево | Ключ | Найден | Шаги поиска");
        search("BST", new BinarySearchTree<>(), values);
        search("AVL", new AVL<>(), values);
        search("RBT", new RBT<>(), values);
    }

    private static void search(String title, BinaryTree<Integer> tree, List<Integer> values) {
        values.forEach(tree::add);
        Counter counter = Counter.create();
        for (int key : List.of(8, 150)) {
            counter.reset();
            boolean found = tree.search(key, counter).isPresent();
            if (found != values.contains(key)) {
                throw new IllegalStateException("Search disagrees with the input values");
            }
            System.out.println(title + " | " + key + " | " + found + " | " + counter.count());
        }
    }
}
