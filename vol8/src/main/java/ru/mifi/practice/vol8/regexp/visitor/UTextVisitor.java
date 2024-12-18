package ru.mifi.practice.vol8.regexp.visitor;

import ru.mifi.practice.vol8.regexp.Tree;

import java.util.ArrayDeque;
import java.util.Deque;

public final class UTextVisitor extends AbstractStringVisitor {
    private final Deque<Boolean> nextOr = new ArrayDeque<>();
    private final Deque<Boolean> nextSet = new ArrayDeque<>();

    @Override
    public void enter(Tree.Or or) {
        buffer.append("(");
        nextOr.push(false);
    }

    @Override
    public void enter(Tree.Unary unary) {
        switch (unary.operator()) {
            case STAR -> buffer.append("{");
            case PLUS, QUESTION -> buffer.append("[");
            default -> throw new IllegalStateException("Unexpected operator: " + unary.operator());
        }
    }

    @Override
    public void enter(Tree.Group group) {
        buffer.append("(");
    }

    @Override
    public void enter(Tree.Set set) {
        buffer.append("(");
        nextSet.add(false);
    }

    @Override
    public void exit(Tree.Or or) {
        buffer.append(")");
        nextOr.pop();
    }

    @Override
    public void exit(Tree.Unary unary) {
        switch (unary.operator()) {
            case STAR -> buffer.append("}");
            case PLUS, QUESTION -> buffer.append("]");
            default -> throw new IllegalStateException("Unexpected operator: " + unary.operator());
        }
    }

    @Override
    public void exit(Tree.Group group) {
        buffer.append(")");
    }

    @Override
    public void exit(Tree.Set set) {
        buffer.append(")");
        nextSet.pop();
    }

    @Override
    public void start() {
        super.start();
        nextSet.clear();
        nextOr.clear();
        buffer.append("@startebnf").append("\n");
        buffer.append("pattern = ");
    }

    @Override
    public void end() {
        buffer.append(";").append("\n");
        buffer.append("@endebnf").append("\n");
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
    public void nextAnd() {
        buffer.append(",");
    }

    @Override
    public void nextSet() {
        Boolean pop = nextSet.pop();
        if (pop != null && pop) {
            buffer.append("|");
        }
        nextSet.push(true);
    }

    @Override
    public void nextRange() {
        buffer.append("-");
    }
}
