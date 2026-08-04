package ru.mifi.practice.vol8.regexp.machine;

import ru.mifi.practice.vol8.regexp.tree.Tree;

import java.util.LinkedList;

/** Построение конечного автомата обходом синтаксического дерева регулярного выражения. */
@SuppressWarnings("PMD.LooseCoupling")
public final class MachineGenerator implements Tree.Visitor {
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
    public void enter(Tree.And and) {
        if (states.isEmpty()) {
            states.add(manager.sequence());
            return;
        }
        State last = states.getLast();
        if (last instanceof State.Sequence) {
            return;
        }
        states.add(manager.sequence());
    }

    @Override
    public void enter(Tree.Group group) {
        states.add(manager.group());
    }

    @Override
    public void enter(Tree.Or or) {
        if (states.isEmpty()) {
            states.add(manager.parallel());
            return;
        }
        State last = states.getLast();
        if (last instanceof State.Parallel) {
            return;
        }
        states.add(manager.parallel());
    }

    @Override
    public void enter(Tree.Set set) {
        states.add(manager.parallel());
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

    /**
     * Отрицание множества оборачивает собранные альтернативы в состояние «кроме»: оно принимает
     * символ, которого нет среди перечисленных. Раньше признак positive не читался вовсе,
     * поэтому «[^of]» вело себя как «[of]».
     */
    @Override
    public void exit(Tree.Set set) {
        exitParallel();
        if (!set.positive()) {
            State excluded = last();
            states.add(manager.excluding(excluded));
        }
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
            case STAR -> states.add(manager.noneOrMore(state));
            case PLUS -> states.add(manager.oneOrMore(state));
            case QUESTION -> states.add(manager.noneOrOne(state));
            default -> throw new IllegalStateException("Unexpected operator: " + unary.operator());
        }
    }

    /**
     * Диапазон раскрывается в набор альтернатив по всем символам от начала до конца. Так он
     * ложится на любой алфавит: каждый символ отображается по отдельности, тогда как сравнивать
     * границы уже отображённых значений было бы бессмысленно. Границы читаются прямо из узла
     * дерева, а состояния, порождённые обходом концов, отбрасываются.
     */
    @Override
    public void exit(Tree.Range range) {
        last();
        last();
        char start = bound(range.start(), range);
        char end = bound(range.end(), range);
        if (start > end) {
            throw new IllegalArgumentException("Начало диапазона больше конца: " + range);
        }
        State.Parallel parallel = manager.parallel();
        for (char ch = start; ch <= end; ch++) {
            parallel.add(manager.symbol(ch));
        }
        states.add(parallel);
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
            parallel.merge(pal);
        } else {
            parallel.add(state);
        }
        states.add(parallel);
    }

    @Override
    public void visit(Tree.Char ch) {
        states.add(manager.symbol(ch.ch()));
    }

    @Override
    public void visit(Tree.Escape escape) {
        states.add(manager.symbol(escape.ch()));
    }

    @Override
    public void any() {
        states.add(manager.any());
    }

    private char bound(Tree.Node node, Tree.Range range) {
        if (node instanceof Tree.Char ch) {
            return ch.ch();
        }
        if (node instanceof Tree.Escape escape) {
            return escape.ch();
        }
        throw new IllegalArgumentException("Границей диапазона может быть только символ: " + range);
    }

    public State getState() {
        return states.getLast();
    }

    @SuppressWarnings("unchecked")
    private <T extends State> T last() {
        return (T) states.removeLast();
    }
}
