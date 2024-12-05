package ru.mifi.practice.vol6.tree;

public interface Visitor<T> {
    void enterNode(Node<T> node);
    void exitNode(Node<T> node);

    void empty();

    @FunctionalInterface
    interface Visit<T> {
        void visit(Visitor<T> visitor, VisitorStrategy<T> strategy);
    }

    final class Stdout<T> implements Visitor<T> {

        @Override
        public void enterNode(Node<T> node) {
            System.out.print(node);
        }

        @Override
        public void exitNode(Node<T> node) {

        }

        @Override
        public void empty() {
            System.out.print("empty");
        }
    }
}
