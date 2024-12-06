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

    void delete(T value);

    Tree<T> copy(Function<? super T, ? extends T> copyFunction);

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

        @SuppressWarnings("PMD.EmptyControlStatement")
        @Override
        public void delete(T value) {
            Node<T> node = find(value);
            if (node == null) {
                //None
            } else if (node.parent() != null) {
                var parent = node.parent();
                if (parent.left() != null && parent.left().value().equals(value)) {
                    parent.left(null);
                } else if (parent.right() != null && parent.right().value().equals(value)) {
                    parent.right(null);
                }
            }
        }

        @Override
        public Tree<T> copy(Function<? super T, ? extends T> copyFunction) {
            Standard<T> tree = new Standard<>(comparator);
            this.visit(new Visitor<T>() {
                @Override
                public void enterNode(Node<T> node) {
                    Node<T> find = tree.find(node.value());
                    T left = node.left() == null ? null : copyFunction.apply(node.left().value());
                    T right = node.right() == null ? null : copyFunction.apply(node.right().value());
                    if (find == null) {
                        tree.add(node.value(), left, right);
                    } else {
                        find.left(left);
                        find.right(right);
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
            }, new VisitorStrategy.PreOrder<>());
            return tree;
        }
    }
}
