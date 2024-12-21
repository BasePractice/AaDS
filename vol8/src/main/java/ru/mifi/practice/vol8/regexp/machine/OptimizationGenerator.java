package ru.mifi.practice.vol8.regexp.machine;

import java.util.LinkedList;

//FIXME: Реализовать оптимизацию автомата
@SuppressWarnings({"PMD.LooseCoupling", "PMD.UnusedPrivateField", "PMD.UnusedLocalVariable"})
public final class OptimizationGenerator extends Visitor.AbstractVisitor {
    private final Manager manager;
    private final LinkedList<State> stack = new LinkedList<>();
    private State root;

    public OptimizationGenerator(Manager manager) {
        this.manager = manager;
    }

    public OptimizationGenerator() {
        this(new Manager.Default());
    }

    @Override
    public void visit(State from, State state) {
        State epsilon = stack.getLast();
    }

    @Override
    public void start() {
        root = null;
        manager.reset();
        stack.clear();
        stack.add(manager.newState(State.Epsilon.class));
    }

    public void start(State state) {
        start();
        root = state;
        state.visit(this);
        end();
    }
}
