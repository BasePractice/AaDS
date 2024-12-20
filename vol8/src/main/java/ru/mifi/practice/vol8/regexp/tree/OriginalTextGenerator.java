package ru.mifi.practice.vol8.regexp.tree;

import java.util.ArrayDeque;
import java.util.Deque;

public final class OriginalTextGenerator extends AbstractStringVisitor {
    private final Deque<Boolean> nextOr = new ArrayDeque<>();

    @Override
    public void enter(Tree.Or or) {
        nextOr.push(false);
    }

    @Override
    public void enter(Tree.Group group) {
        buffer.append("(");
    }

    @Override
    public void enter(Tree.Set set) {
        buffer.append("[");
    }

    @Override
    public void exit(Tree.Or or) {
        nextOr.pop();
    }

    @Override
    public void exit(Tree.Unary unary) {
        switch (unary.operator()) {
            case STAR -> buffer.append("*");
            case PLUS -> buffer.append("+");
            case QUESTION -> buffer.append("?");
            default -> throw new IllegalStateException("Unexpected operator: " + unary.operator());
        }
    }

    @Override
    public void exit(Tree.Group group) {
        buffer.append(")");
    }

    @Override
    public void exit(Tree.Set set) {
        buffer.append("]");
    }

    @Override
    public void any() {
        buffer.append(".");
    }

    @Override
    public void nextOr() {
        Boolean pop = nextOr.pop();
        if (pop != null && pop) {
            buffer.append("|");
        }
        nextOr.push(true);
    }

    @Override
    public void nextRange() {
        buffer.append("-");
    }
}
