package ru.mifi.practice.vol8.regexp.visitor;

import ru.mifi.practice.vol8.regexp.Mach;
import ru.mifi.practice.vol8.regexp.Tree;

import java.util.ArrayDeque;
import java.util.Deque;

import static ru.mifi.practice.vol8.regexp.Mach.epsilon;

public final class MatchGenerator extends AbstractVisitor {
    private final Deque<Mach.State> stack = new ArrayDeque<>();

    @Override
    public void visit(Tree.Char ch) {
        stack.add(Mach.matched(ch.ch()));
    }

    @Override
    public void start() {
        stack.clear();
    }

    @Override
    public void nextOr() {
        Mach.State state = stack.removeLast();
        if (state instanceof Mach.Epsilon) {
            stack.add(state);
            return;
        }
        Mach.State c = stack.removeLast();
        if (c instanceof Mach.Epsilon epsilon) {
            epsilon.add(c);
            stack.add(epsilon);
            return;
        }
        throw new IllegalStateException("Unexpected state: " + state);
    }

    @Override
    public void enter(Tree.Or or) {
        stack.add(epsilon());
    }

    @Override
    public void exit(Tree.Unary unary) {
        Mach.State state = stack.removeLast();
        Mach.State start = epsilon();
        Mach.State end = epsilon(true);
        switch (unary.operator()) {
            case STAR -> {
                start.add(end);
                start.add(state);
                state.add(end);
                state.add(start);
            }
            case PLUS -> {
                start.add(state);
                state.add(end);
                end.add(state);
            }
            case QUESTION -> {
                start.add(end);
                start.add(state);
                state.add(end);
            }
            default -> throw new IllegalStateException("Unexpected operator: " + unary.operator());
        }
        stack.add(start);
    }

    @Override
    public void exit(Tree.And and) {
        Mach.State right = stack.removeLast();
        Mach.State left = stack.removeLast();
        if (right instanceof Mach.Sequence sequence) {
            sequence.add(left);
            stack.push(right);
        } else if (left instanceof Mach.Sequence sequence) {
            sequence.add(right);
            stack.push(left);
        } else {
            var sequence = Mach.sequence();
            sequence.add(right);
            sequence.add(left);
            stack.push(sequence);
        }
    }

    @Override
    public void end() {
        System.out.println();
    }

    public Mach getMach() {
        return Mach.of(stack.pop());
    }
}
