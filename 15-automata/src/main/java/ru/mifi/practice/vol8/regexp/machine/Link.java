package ru.mifi.practice.vol8.regexp.machine;

/**
 * Связь состояния со следующим за ним. Держит номер состояния и переход, а заодно отвечает за
 * поведение пустого состояния, которое само по себе не принимает ни одного символа. Каждое
 * состояние автомата складывается из такой связи и собственного правила приёма.
 */
final class Link {
    private final int index;
    private State next;

    Link(int index) {
        this.index = index;
    }

    int index() {
        return index;
    }

    State next() {
        return next;
    }

    void setNext(State state) {
        this.next = state;
    }

    boolean accept(Input input) {
        return false;
    }

    State.Match match(Input input) {
        return State.Match.failure(input);
    }

    State lastState() {
        return next;
    }

    String diagramLabel() {
        return "Epsilon";
    }

    void describe(State.Diagram diagram, String stateName, String nextName) {
        //Nothing
    }

    /**
     * Съедает один символ и передаёт управление продолжению. Если продолжение есть, но входа
     * ему не хватает, это отказ, а не успех: иначе шаблон совпадал бы со своим префиксом.
     */
    State.Match consume(State owner, Input input) {
        if (!owner.accept(input)) {
            return State.Match.failure(input);
        }
        input.next();
        if (next == null) {
            return State.Match.ok(input);
        }
        if (next.accept(input)) {
            return next.match(input);
        }
        return State.Match.failure(input);
    }

    void visitNext(Visitor visitor, State owner) {
        visitor.visit(owner, next);
        if (next != null) {
            next.visit(visitor);
        }
    }

    void visitBranch(Visitor visitor, State owner, State branch) {
        visitor.visit(owner, branch);
        branch.visit(visitor);
        if (next != null) {
            visitor.visit(branch, next);
            next.visit(visitor);
        }
    }

    @Override
    public String toString() {
        if (next != null) {
            return " --> " + next;
        }
        return "";
    }
}
