package ru.mifi.practice.vol8.regexp.machine;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

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

    public record Match(boolean ok, Input input) {

        static Match ok(Input input) {
            return new Match(true, input.copy());
        }

        static Match failure(Input input) {
            return new Match(false, input.copy());
        }

    }

    public static final class Symbol extends State {
        final char symbol;

        private Symbol(Manager manager, int index, Character symbol) {
            super(manager, index);
            this.symbol = symbol;
        }

        @Override
        public boolean accept(Input input) {
            return input.peek().map(c -> c == symbol).orElse(false);
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                input.next();
                if (next != null && next.accept(input)) {
                    return next.match(input);
                }
                return Match.ok(input);
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
    }

    static final class Group extends State {
        private Group(Manager manager, int index) {
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

        private Sequence(Manager manager, int index) {
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
            return start.toString() + super.toString();
        }
    }

    public static final class Epsilon extends Parallel {
        private Epsilon(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public boolean accept(Input input) {
            return true;
        }
    }

    public static class Parallel extends State {
        final List<State> states = new ArrayList<>();

        private Parallel(Manager manager, int index) {
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
                    //TODO: Реализовать для всех оставшихся путей
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
        private NoneOrOne(Manager manager, int index, State state) {
            super(manager, index, state);
        }

        //FIXME: Проверить правильность
        @Override
        public boolean accept(Input input) {
            return true;
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
    }

    public static final class NoneOrMore extends SingleState {
        private NoneOrMore(Manager manager, int index, State state) {
            super(manager, index, state);
        }

        @Override
        public boolean accept(Input input) {
            return true;
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
    }

    public static final class OneOrMore extends SingleState {
        private OneOrMore(Manager manager, int index, State state) {
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
                    Input prev = copy;
                    Match next = matched;
                    while (next.ok()) {
                        prev = next.input;
                        next = state.match(next.input);
                    }
                    copy = prev;
                    if (this.next != null && this.next.accept(copy)) {
                        return this.next.match(copy);
                    }
                    return matched;
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
    }
}
