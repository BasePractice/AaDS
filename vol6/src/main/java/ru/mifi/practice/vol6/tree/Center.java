package ru.mifi.practice.vol6.tree;

import ru.mifi.practice.vol6.tree.visitors.Count;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Почему реализация не правильная?
 *
 * @param <T> тип значения в вершине дерева
 */
public final class Center<T> {
    public List<Node<T>> center(Tree<T> tree, Function<? super T, ? extends T> copyFunction) {
        tree = tree.copy(copyFunction);
        List<Node<T>> result = new ArrayList<>();
        Set<Node<T>> leafs = new HashSet<>();
        VisitorStrategy.PreOrder<T> strategy = new VisitorStrategy.PreOrder<>();
        Visitor<T> search = new Visitor<>() {
            @Override
            public void enterNode(Node<T> node) {
                if (node.left() == null && node.right() == null) {
                    leafs.add(node);
                }
            }

            @Override
            public void exitNode(Node<T> node) {
                //None
            }

            @Override
            public void empty() {
                //None
            }
        };
        Count<T> count = new Count<>();
        while (true) {
            count.clear();
            tree.visit(count, strategy);
            if (count.count() <= 2) {
                break;
            }
            leafs.clear();
            tree.visit(search, strategy);
            for (Node<T> leaf : leafs) {
                tree.delete(leaf.value());
            }
        }
        tree.visit(new Visitor<T>() {

            @Override
            public void enterNode(Node<T> node) {
                result.add(node);
            }

            @Override
            public void exitNode(Node<T> node) {
                //None
            }

            @Override
            public void empty() {
                //None
            }
        }, strategy);
        return result;
    }
}
