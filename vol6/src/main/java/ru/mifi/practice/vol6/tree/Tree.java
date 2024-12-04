package ru.mifi.practice.vol6.tree;

import java.util.Comparator;

public interface Tree<T> extends Visitor.Visit<T> {

    void add(T element);

    @Override
    void visit(Visitor<T> visitor, VisitorStrategy<T> strategy);

    abstract class AbstractTree<T> implements Tree<T> {
        protected final Comparator<T> comparator;
        protected Node<T> root;

        protected AbstractTree(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public void add(T element) {
            root = root == null ? Node.root(element) : add(root, element);
        }

        abstract Node<T> add(Node<T> root, T element);

        @Override
        public void visit(Visitor<T> visitor, VisitorStrategy<T> strategy) {
            if (root != null) {
                strategy.visit(root, visitor, strategy);
            } else {
                visitor.empty();
            }
        }
    }
}
