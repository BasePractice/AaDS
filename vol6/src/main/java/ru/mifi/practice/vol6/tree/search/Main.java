package ru.mifi.practice.vol6.tree.search;

import ru.mifi.practice.vol6.Counter;

import java.util.function.Consumer;

public abstract class Main {
    public static void main(String[] args) {
        Consumer<BinaryTree<Integer>> generator = tree -> tree.add(8).add(3).add(6).add(1).add(4).add(7).add(10).add(14).add(13);
        test("Binary", new BinarySearchTree<>(), generator);
        test("AVL", new AVL<>(), generator);
        System.out.println("Вырожденное дерево");
        generator = tree -> tree.add(1).add(2).add(3).add(4).add(5).add(6).add(7).add(8).add(9);
        test("Binary", new BinarySearchTree<>(), generator);
        test("AVL", new AVL<>(), generator);
    }

    private static void test(String title, BinaryTree<Integer> tree, Consumer<BinaryTree<Integer>> generator) {
        System.out.println("Tree   : " + title);
        Counter counter = new Counter.Default();
        generator.accept(tree);
        System.out.println("BinTree: " + tree);
        System.out.println("Search : " + tree.search(8, counter));
        System.out.println("Steps  : " + counter);
        counter.reset();
        System.out.println("Search : " + tree.search(150, counter));
        System.out.println("Steps  : " + counter);
        tree.delete(7);
        System.out.println("Delete : " + 7);
        System.out.println("BinTree: " + tree);
        tree.delete(9);
        System.out.println("Delete : " + 9);
        System.out.println("BinTree: " + tree);
        counter.reset();
    }
}
