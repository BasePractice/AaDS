package ru.mifi.practice.vol8.regexp.machine;

import ru.mifi.practice.vol8.regexp.tree.Tree;
import ru.mifi.practice.vol8.regexp.tree.AbstractVisitor;

import java.util.LinkedList;

@SuppressWarnings("PMD.LooseCoupling")
public final class MachineGenerator extends AbstractVisitor {
    private final LinkedList<State> states = new LinkedList<>();
    private final Manager manager;

    public MachineGenerator(Manager manager) {
        this.manager = manager;
    }

    public MachineGenerator() {
        this(new Manager.Default());
    }

    @Override
    public void start() {
        states.clear();
    }

    @Override
    public void end() {
        System.out.println(getState());
    }

    @Override
    public void enter(Tree.And and) {
        if (states.isEmpty()) {
            states.add(manager.newState(State.Sequence.class));
            return;
        }
        State last = states.getLast();
        if (last instanceof State.Sequence) {
            return;
        }
        states.add(manager.newState(State.Sequence.class));
    }

    @Override
    public void enter(Tree.Group group) {
        states.add(manager.newState(State.Group.class));
    }

    @Override
    public void enter(Tree.Or or) {
        states.add(manager.newState(State.Parallel.class));
    }

    @Override
    public void enter(Tree.Set set) {
        states.add(manager.newState(State.Parallel.class));
    }

    @Override
    public void exit(Tree.And and) {
        var state = last();
        if (states.isEmpty()) {
            states.add(state);
            return;
        }
        var last = last();
        if (last instanceof State.Sequence sequence) {
            sequence.add(state);
            states.add(sequence);
        } else if (last instanceof State.Parallel parallel) {
            parallel.add(state);
            states.add(parallel);
        } else if (last instanceof State.Group) {
            states.add(last);
            states.add(state);
        } else {
            last.setNext(state);
            states.add(last);
        }
    }

    @Override
    public void exit(Tree.Or or) {
        exitParallel();
    }

    @Override
    public void exit(Tree.Set set) {
        exitParallel();
    }

    @Override
    public void exit(Tree.Group group) {
        State state = last();
        State last = last();
        if (last instanceof State.Group) {
            states.add(state);
        } else {
            states.add(last);
            states.add(state);
        }
    }

    @Override
    public void exit(Tree.Unary unary) {
        State state = last();
        switch (unary.operator()) {
            case STAR -> states.add(manager.newState(State.NoneOrMore.class, state));
            case PLUS -> states.add(manager.newState(State.OneOrMore.class, state));
            case QUESTION -> states.add(manager.newState(State.NoneOrOne.class, state));
            default -> throw new IllegalStateException("Unexpected operator: " + unary.operator());
        }
    }

    @Override
    public void nextAnd() {
        State state = last();
        State.Sequence sequence = last();
        sequence.add(state);
        states.add(sequence);
    }

    @Override
    public void nextOr() {
        State state = last();
        if (state instanceof State.Parallel parallel) {
            states.add(parallel);
        } else {
            State.Parallel parallel = last();
            parallel.add(state);
            states.add(parallel);
        }
    }

    @Override
    public void nextSet() {
        nextOr();
    }

    private void exitParallel() {
        State state = last();
        if (state instanceof State.Parallel && states.isEmpty()) {
            states.add(state);
            return;
        }
        if (!(states.getLast() instanceof State.Parallel)) {
            states.add(state);
            return;
        }
        State.Parallel parallel = last();
        if (state instanceof State.Parallel pal) {
            pal.states.forEach(parallel::add);
        } else {
            parallel.add(state);
        }
        states.add(parallel);
    }

    @Override
    public void visit(Tree.Char ch) {
        states.add(manager.newState(State.Symbol.class, ch.ch()));
    }

    public State getState() {
        return states.getLast();
    }

    @SuppressWarnings("unchecked")
    private <T extends State> T last() {
        return (T) states.removeLast();
    }
}
