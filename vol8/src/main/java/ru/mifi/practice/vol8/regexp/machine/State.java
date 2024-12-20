package ru.mifi.practice.vol8.regexp.machine;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String toString() {
        if (next != null) {
            return " --> " + next;
        }
        return "";
    }

    public static final class Symbol extends State {
        final char symbol;

        private Symbol(Manager manager, int index, Character symbol) {
            super(manager, index);
            this.symbol = symbol;
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
    }

    public static class Parallel extends State {
        final List<State> states = new ArrayList<>();

        private Parallel(Manager manager, int index) {
            super(manager, index);
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
