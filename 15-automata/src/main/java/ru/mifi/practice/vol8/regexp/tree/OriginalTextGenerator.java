package ru.mifi.practice.vol8.regexp.tree;

import ru.mifi.practice.vol8.regexp.TextBuffer;

import java.util.ArrayDeque;
import java.util.Deque;

/** Восстановление исходного текста регулярного выражения обходом дерева. */
public final class OriginalTextGenerator implements Tree.Visitor {
    private final TextBuffer buffer = new TextBuffer();
    private final Deque<Boolean> nextOr = new ArrayDeque<>();

    @Override
    public void start() {
        buffer.reset();
    }

    @Override
    public void visit(Tree.Char ch) {
        buffer.append(ch.ch());
    }

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
        if (!set.positive()) {
            buffer.append("^");
        }
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

    @Override
    public String toString() {
        return buffer.toString();
    }
}
