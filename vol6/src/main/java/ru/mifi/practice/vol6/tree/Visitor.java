package ru.mifi.practice.vol6.tree;

public interface Visitor<T> {
    void visit(Node<T> node);

    void empty();

    @FunctionalInterface
    interface Visit<T> {
        void visit(Visitor<T> visitor, VisitorStrategy<T> strategy);
    }

    final class Stdout<T> implements Visitor<T> {

        @Override
        public void visit(Node<T> node) {
            System.out.println(node);
        }

        @Override
        public void empty() {
            System.out.println("empty");
        }
    }
}
