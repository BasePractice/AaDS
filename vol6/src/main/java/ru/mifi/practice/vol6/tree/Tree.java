package ru.mifi.practice.vol6.tree;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.function.Function;

public interface Tree<T> extends Visitor.Visit<T> {

    void add(T element);

    Node<T> find(T element);

    @Override
    void visit(Visitor<T> visitor, VisitorStrategy<T> strategy);

    interface Loader<T> {
        Tree<T> parse(InputStream stream, Function<String, T> value, Comparator<T> comparator) throws IOException;
    }

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

    final class Standard<T> extends AbstractTree<T> implements Tree<T> {
        public Standard(Comparator<T> comparator) {
            super(comparator);
        }

        @Override
        Node<T> add(Node<T> root, T element) {
            return null;
        }

        public void add(T owner, T left, T right) {
            if (root == null) {
                root = Node.root(owner);
                root.left(left);
                root.right(right);
            } else {
                Node<T> find = root.search(owner);
                if (find == null) {
                    throw new IllegalArgumentException("No such element: " + owner);
                }
                find.left(left);
                find.right(right);
            }
        }

        @Override
        public Node<T> find(T element) {
            if (root == null) {
                return null;
            }
            return root.search(element);
        }
    }
}
