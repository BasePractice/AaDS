package ru.mifi.practice.vol6.tree;

@FunctionalInterface
public interface VisitorStrategy<T> {
    void visit(Node<T> node, Visitor<T> visitor, VisitorStrategy<T> strategy);

    final class PreOrder<T> implements VisitorStrategy<T> {
        @Override
        public void visit(Node<T> node, Visitor<T> visitor, VisitorStrategy<T> strategy) {
            if (node == null) {
                return;
            }
            visitor.enterNode(node);
            strategy.visit(node.left(), visitor, strategy);
            strategy.visit(node.right(), visitor, strategy);
            visitor.exitNode(node);
        }
    }

    final class PostOrder<T> implements VisitorStrategy<T> {
        @Override
        public void visit(Node<T> node, Visitor<T> visitor, VisitorStrategy<T> strategy) {
            if (node == null) {
                return;
            }
            strategy.visit(node.left(), visitor, strategy);
            strategy.visit(node.right(), visitor, strategy);
            visitor.enterNode(node);
            visitor.exitNode(node);
        }
    }

    final class InOrder<T> implements VisitorStrategy<T> {
        @Override
        public void visit(Node<T> node, Visitor<T> visitor, VisitorStrategy<T> strategy) {
            if (node == null) {
                return;
            }
            strategy.visit(node.left(), visitor, strategy);
            visitor.enterNode(node);
            strategy.visit(node.right(), visitor, strategy);
            visitor.exitNode(node);
        }
    }
}
