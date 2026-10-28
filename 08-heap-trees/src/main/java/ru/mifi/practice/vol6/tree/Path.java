package ru.mifi.practice.vol6.tree;

import java.util.List;

/** Путь между двумя значениями внутри дерева. */
public interface Path<T> {
    List<Node<T>> path(Tree<T> tree, T start, T end);
}
