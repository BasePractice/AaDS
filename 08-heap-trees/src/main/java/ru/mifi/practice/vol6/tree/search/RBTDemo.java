package ru.mifi.practice.vol6.tree.search;

import ru.mifi.practice.commons.Counter;

import java.util.List;

/** Пошаговые примеры вставки и удаления из лекции о красно-чёрном дереве. */
public final class RBTDemo {
    private RBTDemo() {
    }

    public static void main(String[] args) {
        example("Поворот и перекраска", List.of(10, 5, 1, 15));
        example("Два поворота для треугольника", List.of(10, 5, 7));
        RBT<Integer> tree = example("Удаление чёрного листа", List.of(10, 5, 15, 20));
        Counter counter = Counter.create();
        tree.delete(5, counter);
        System.out.println("Удалили 5: " + shape(tree.root) + ", шагов: " + counter.count());
        counter.reset();
        System.out.println("Поиск 20: " + tree.search(20, counter).isPresent() + ", шагов: " + counter.count());
        counter.reset();
        System.out.println("Поиск 5: " + tree.search(5, counter).isPresent() + ", шагов: " + counter.count());
    }

    private static RBT<Integer> example(String title, List<Integer> keys) {
        RBT<Integer> tree = new RBT<>();
        System.out.println(title + "; Ч — чёрный, К — красный; [левый, правый].");
        Counter counter = Counter.create();
        for (int key : keys) {
            counter.reset();
            tree.add(key, counter);
            System.out.println("Добавили " + key + ": " + shape(tree.root) + ", шагов: " + counter.count());
        }
        return tree;
    }

    private static String shape(BinaryTree.Node<Integer> node) {
        if (node == null) {
            return "NIL";
        }
        return node.value + (node.custom == 1 ? "Ч" : "К")
            + "[" + shape(node.left) + ", " + shape(node.right) + "]";
    }
}
