package ru.mifi.practice.vol8.regexp.machine;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

//FIXME: Переписать проверку в FSM не через рекурсию — на длинном вводе рекурсивный спуск по состояниям грозит переполнением стека
/** Состояние конечного автомата для сопоставления регулярного выражения. */
@SuppressWarnings("PMD.SimplifyBooleanReturnss")
@EqualsAndHashCode(of = "index")
public abstract class State {
    final Manager manager;
    final int index;
    protected State parent;
    protected State next;

    protected State(Manager manager, int index) {
        this.manager = manager;
        this.index = index;
    }

    public abstract void visit(Visitor visitor);

    protected void setNext(State next) {
        this.next = next;
    }

    public boolean accept(Input input) {
        return false;
    }

    @Override
    public String toString() {
        if (next != null) {
            return " --> " + next;
        }
        return "";
    }

    public Match match(Input input) {
        return new Match(false, input.copy());
    }

    State lastState() {
        return next;
    }

    String diagramLabel() {
        return "Epsilon";
    }

    void describe(Diagram diagram, String stateName, String nextName) {
        //Nothing
    }

    public interface Diagram {
        String name(State state);

        void edge(String from, String to);
    }

    public record Match(boolean ok, Input input) {

        static Match ok(Input input) {
            return new Match(true, input.copy());
        }

        static Match failure(Input input) {
            return new Match(false, input.copy());
        }

        boolean isCompleted() {
            return !input.hasNext();
        }
    }

    public static final class Symbol extends State {
        final Object symbol;

        Symbol(Manager manager, int index, Object symbol) {
            super(manager, index);
            this.symbol = symbol;
        }

        @Override
        public boolean accept(Input input) {
            return input.peek().map(c -> c.equals(symbol)).orElse(false);
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                input.next();
                if (next == null) {
                    return Match.ok(input);
                }
                if (next.accept(input)) {
                    return next.match(input);
                }
                return Match.failure(input);
            }
            return Match.failure(input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, next);
            if (next != null) {
                next.visit(visitor);
            }
        }

        @Override
        public String toString() {
            return symbol + super.toString();
        }

        @Override
        String diagramLabel() {
            return String.valueOf(symbol);
        }
    }

    static final class Group extends State {
        Group(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public void visit(Visitor visitor) {
            //Nothing
        }
    }

    public static final class Sequence extends State {
        State last;
        private State start;

        Sequence(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public boolean accept(Input input) {
            return start.accept(input);
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                var matched = start.match(input);
                if (matched.ok() && next != null && next.accept(matched.input)) {
                    return next.match(matched.input);
                }
                return matched;
            }
            return Match.failure(input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, start);
            start.visit(visitor);
            if (next != null) {
                next.visit(visitor);
            }
        }

        void add(State state) {
            state.parent = this;
            if (start == null) {
                start = state;
            } else {
                state.parent = last;
                last.setNext(state);
            }
            last = state;
        }

        @Override
        public String toString() {
            String string = "";
            if (start != null) {
                string = start.toString();
            }
            return string + super.toString();
        }

        @Override
        State lastState() {
            return last;
        }
    }

    /**
     * Точка регулярного выражения: принимает любой символ, лишь бы он был.
     */
    public static final class Any extends State {
        Any(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public boolean accept(Input input) {
            return input.hasNext();
        }

        @Override
        public Match match(Input input) {
            return consume(this, next, input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, next);
            if (next != null) {
                next.visit(visitor);
            }
        }

        @Override
        public String toString() {
            return "." + super.toString();
        }
    }

    /**
     * Отрицание множества: принимает символ, которого нет среди перечисленных.
     */
    public static final class Excluding extends State {
        private final State excluded;

        Excluding(Manager manager, int index, State excluded) {
            super(manager, index);
            this.excluded = excluded;
        }

        @Override
        public boolean accept(Input input) {
            return input.hasNext() && !excluded.accept(input);
        }

        @Override
        public Match match(Input input) {
            return consume(this, next, input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, next);
            if (next != null) {
                next.visit(visitor);
            }
        }

        @Override
        public String toString() {
            return "[^" + excluded + "]" + super.toString();
        }
    }

    /**
     * Съедает один символ и передаёт управление продолжению. Если продолжение есть, но входа
     * ему не хватает, это отказ, а не успех: иначе шаблон совпадал бы со своим префиксом.
     */
    private static Match consume(State current, State next, Input input) {
        if (!current.accept(input)) {
            return Match.failure(input);
        }
        input.next();
        if (next == null) {
            return Match.ok(input);
        }
        if (next.accept(input)) {
            return next.match(input);
        }
        return Match.failure(input);
    }

    public static final class Epsilon extends Parallel {
        private Epsilon(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public boolean accept(Input input) {
            return input.hasNext();
        }
    }

    public static class Parallel extends State {
        final List<State> states = new ArrayList<>();

        Parallel(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public boolean accept(Input input) {
            List<State> accepted = getAccepted(input);
            return !accepted.isEmpty();
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                List<State> accepted = getAccepted(input);
                for (State next : accepted) {
                    Input copy = input.copy();
                    var accept = next.match(copy);
                    //TODO: Реализовать для всех оставшихся путей — берётся первая принявшая ветка, остальные не перебираются
                    if (accept.ok()) {
                        if (this.next != null && next.accept(accept.input)) {
                            return next.match(accept.input);
                        }
                        return accept;
                    }
                }
            }
            return Match.failure(input);
        }

        private List<State> getAccepted(Input input) {
            return states.stream().filter(c -> c.accept(input)).toList();
        }

        @Override
        public void visit(Visitor visitor) {
            for (State state : states) {
                visitor.visit(this, state);
                state.visit(visitor);
            }
            if (next != null) {
                next.visit(visitor);
            }
        }

        void add(State state) {
            state.parent = this;
            states.add(state);
        }

        @Override
        protected void setNext(State next) {
            states.forEach(s -> s.setNext(next));
        }

        @Override
        public String toString() {
            return states + super.toString();
        }

        @Override
        State lastState() {
            return this;
        }
    }

    @SuppressWarnings("PMD.ModifierOrder")
    private abstract static class SingleState extends State {
        protected final State state;

        protected SingleState(Manager manager, int index, State state) {
            super(manager, index);
            this.state = state;
        }
    }

    public static final class NoneOrOne extends SingleState {
        NoneOrOne(Manager manager, int index, State state) {
            super(manager, index, state);
        }

        //FIXME: Проверить правильность — accept() истинно при любом непустом вводе, даже когда ни сам элемент, ни продолжение его не принимают
        @Override
        public boolean accept(Input input) {
            return input.hasNext();
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                Input copy = input.copy();
                if (state.accept(copy)) {
                    var matched = state.match(copy);
                    if (matched.ok() && next != null && next.accept(matched.input)) {
                        return next.match(matched.input);
                    }
                    return matched;
                } else if (next != null && next.accept(copy)) {
                    return next.match(copy);
                }
            }
            return Match.failure(input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, state);
            state.visit(visitor);
            if (next != null) {
                visitor.visit(state, next);
                next.visit(visitor);
            }
        }

        @Override
        public String toString() {
            return "(" + state + ")?" + super.toString();
        }

        @Override
        void describe(Diagram diagram, String stateName, String nextName) {
            diagram.edge(nextName, diagram.name(next));
            diagram.edge(stateName, diagram.name(next));
        }
    }

    public static final class NoneOrMore extends SingleState {
        NoneOrMore(Manager manager, int index, State state) {
            super(manager, index, state);
        }

        @Override
        public boolean accept(Input input) {
            return input.hasNext();
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                Input copy = input.copy();
                if (state.accept(copy)) {
                    var matched = state.match(copy);
                    if (matched.ok()) {
                        Input prev = copy;
                        while (matched.ok()) {
                            prev = matched.input;
                            matched = state.match(copy);
                        }
                        copy = prev;
                        if (next != null && next.accept(copy)) {
                            return next.match(copy);
                        }
                    }
                    if (next != null && next.accept(matched.input)) {
                        return next.match(matched.input);
                    }
                } else if (next != null && next.accept(copy)) {
                    return next.match(copy);
                }
                return Match.ok(input);
            }
            return Match.failure(input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, state);
            state.visit(visitor);
            if (next != null) {
                visitor.visit(state, next);
                next.visit(visitor);
            }
        }

        @Override
        public String toString() {
            return "(" + state + ")*" + super.toString();
        }

        @Override
        void describe(Diagram diagram, String stateName, String nextName) {
            diagram.edge(diagram.name(state), diagram.name(state));
            diagram.edge(nextName, diagram.name(next));
        }
    }

    public static final class OneOrMore extends SingleState {
        OneOrMore(Manager manager, int index, State state) {
            super(manager, index, state);
        }

        @Override
        public boolean accept(Input input) {
            return state.accept(input);
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                Input copy = input.copy();
                var matched = state.match(copy);
                if (matched.ok()) {
                    Match prev = matched;
                    Match next = matched;
                    while (next.ok()) {
                        prev = next;
                        next = state.match(next.input);
                    }
                    if (this.next != null && this.next.accept(prev.input)) {
                        return this.next.match(prev.input);
                    }
                    return prev;
                }
            }
            return Match.failure(input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, state);
            state.visit(visitor);
            if (next != null) {
                visitor.visit(state, next);
                next.visit(visitor);
            }
        }

        @Override
        public String toString() {
            return "(" + state + ")+" + super.toString();
        }

        @Override
        void describe(Diagram diagram, String stateName, String nextName) {
            diagram.edge(nextName, diagram.name(next));
            diagram.edge(diagram.name(state.lastState()), diagram.name(state));
        }
    }
}
