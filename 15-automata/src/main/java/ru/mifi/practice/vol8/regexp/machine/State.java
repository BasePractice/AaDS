package ru.mifi.practice.vol8.regexp.machine;

import java.util.ArrayList;
import java.util.List;

//FIXME: Переписать проверку в FSM не через рекурсию — на длинном вводе рекурсивный спуск по состояниям грозит переполнением стека
/** Состояние конечного автомата для сопоставления регулярного выражения. */
public interface State {
    void visit(Visitor visitor);

    void setNext(State next);

    boolean accept(Input input);

    Match match(Input input);

    int index();

    State lastState();

    String diagramLabel();

    void describe(Diagram diagram, String stateName, String nextName);

    interface Diagram {
        String name(State state);

        void edge(String from, String to);
    }

    record Match(boolean ok, Input input) {

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

    /** Символ шаблона: принимает ровно то значение, которым создан. */
    final class Symbol implements State {
        private final Link link;
        private final Object symbol;

        Symbol(int index, Object symbol) {
            this.link = new Link(index);
            this.symbol = symbol;
        }

        @Override
        public boolean accept(Input input) {
            return input.peek().map(c -> c.equals(symbol)).orElse(false);
        }

        @Override
        public Match match(Input input) {
            return link.consume(this, input);
        }

        @Override
        public void visit(Visitor visitor) {
            link.visitNext(visitor, this);
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return link.lastState();
        }

        @Override
        public String diagramLabel() {
            return String.valueOf(symbol);
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            link.describe(diagram, stateName, nextName);
        }

        @Override
        public String toString() {
            return symbol + link.toString();
        }
    }

    /** Скобка шаблона: разметка для сборки автомата, в самом сопоставлении не участвует. */
    final class Group implements State {
        private final Link link;

        Group(int index) {
            this.link = new Link(index);
        }

        @Override
        public boolean accept(Input input) {
            return link.accept(input);
        }

        @Override
        public Match match(Input input) {
            return link.match(input);
        }

        @Override
        public void visit(Visitor visitor) {
            //Nothing
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return link.lastState();
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            link.describe(diagram, stateName, nextName);
        }

        @Override
        public String toString() {
            return link.toString();
        }
    }

    /** Цепочка состояний: принимает вход, который её звенья разбирают одно за другим. */
    final class Sequence implements State {
        private final Link link;
        private State start;
        private State last;

        Sequence(int index) {
            this.link = new Link(index);
        }

        @Override
        public boolean accept(Input input) {
            return start.accept(input);
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                var matched = start.match(input);
                State next = link.next();
                if (matched.ok() && next != null && next.accept(matched.input())) {
                    return next.match(matched.input());
                }
                return matched;
            }
            return Match.failure(input);
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this, start);
            start.visit(visitor);
            State next = link.next();
            if (next != null) {
                next.visit(visitor);
            }
        }

        void add(State state) {
            if (start == null) {
                start = state;
            } else {
                last.setNext(state);
            }
            last = state;
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return last;
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            link.describe(diagram, stateName, nextName);
        }

        @Override
        public String toString() {
            String string = "";
            if (start != null) {
                string = start.toString();
            }
            return string + link;
        }
    }

    /**
     * Точка регулярного выражения: принимает любой символ, лишь бы он был.
     */
    final class Any implements State {
        private final Link link;

        Any(int index) {
            this.link = new Link(index);
        }

        @Override
        public boolean accept(Input input) {
            return input.hasNext();
        }

        @Override
        public Match match(Input input) {
            return link.consume(this, input);
        }

        @Override
        public void visit(Visitor visitor) {
            link.visitNext(visitor, this);
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return link.lastState();
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            link.describe(diagram, stateName, nextName);
        }

        @Override
        public String toString() {
            return "." + link;
        }
    }

    /**
     * Отрицание множества: принимает символ, которого нет среди перечисленных.
     */
    final class Excluding implements State {
        private final Link link;
        private final State excluded;

        Excluding(int index, State excluded) {
            this.link = new Link(index);
            this.excluded = excluded;
        }

        @Override
        public boolean accept(Input input) {
            return input.hasNext() && !excluded.accept(input);
        }

        @Override
        public Match match(Input input) {
            return link.consume(this, input);
        }

        @Override
        public void visit(Visitor visitor) {
            link.visitNext(visitor, this);
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return link.lastState();
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            link.describe(diagram, stateName, nextName);
        }

        @Override
        public String toString() {
            return "[^" + excluded + "]" + link;
        }
    }

    /** Развилка шаблона: принимает вход, который берёт хотя бы одна из её веток. */
    final class Parallel implements State {
        private final Link link;
        private final List<State> states = new ArrayList<>();

        Parallel(int index) {
            this.link = new Link(index);
        }

        @Override
        public boolean accept(Input input) {
            return !accepted(input).isEmpty();
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                for (State branch : accepted(input)) {
                    Input copy = input.copy();
                    var accept = branch.match(copy);
                    //TODO: Реализовать для всех оставшихся путей — берётся первая принявшая ветка, остальные не перебираются
                    if (accept.ok()) {
                        if (link.next() != null && branch.accept(accept.input())) {
                            return branch.match(accept.input());
                        }
                        return accept;
                    }
                }
            }
            return Match.failure(input);
        }

        private List<State> accepted(Input input) {
            return states.stream().filter(c -> c.accept(input)).toList();
        }

        @Override
        public void visit(Visitor visitor) {
            for (State state : states) {
                visitor.visit(this, state);
                state.visit(visitor);
            }
            State next = link.next();
            if (next != null) {
                next.visit(visitor);
            }
        }

        void add(State state) {
            states.add(state);
        }

        void merge(Parallel parallel) {
            parallel.states.forEach(this::add);
        }

        @Override
        public void setNext(State next) {
            states.forEach(s -> s.setNext(next));
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return this;
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            link.describe(diagram, stateName, nextName);
        }

        @Override
        public String toString() {
            return states + link.toString();
        }
    }

    /** Необязательное состояние: пропускает своё тело, если вход его не принимает. */
    final class NoneOrOne implements State {
        private final Link link;
        private final State state;

        NoneOrOne(int index, State state) {
            this.link = new Link(index);
            this.state = state;
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
                State next = link.next();
                if (state.accept(copy)) {
                    var matched = state.match(copy);
                    if (matched.ok() && next != null && next.accept(matched.input())) {
                        return next.match(matched.input());
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
            link.visitBranch(visitor, this, state);
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return link.lastState();
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            diagram.edge(nextName, diagram.name(link.next()));
            diagram.edge(stateName, diagram.name(link.next()));
        }

        @Override
        public String toString() {
            return "(" + state + ")?" + link;
        }
    }

    /** Повторение от нуля: съедает столько повторов своего тела, сколько найдёт. */
    final class NoneOrMore implements State {
        private final Link link;
        private final State state;

        NoneOrMore(int index, State state) {
            this.link = new Link(index);
            this.state = state;
        }

        @Override
        public boolean accept(Input input) {
            return input.hasNext();
        }

        @Override
        public Match match(Input input) {
            if (accept(input)) {
                Input copy = input.copy();
                State next = link.next();
                if (state.accept(copy)) {
                    var matched = state.match(copy);
                    if (matched.ok()) {
                        Input prev = copy;
                        while (matched.ok()) {
                            prev = matched.input();
                            matched = state.match(copy);
                        }
                        copy = prev;
                        if (next != null && next.accept(copy)) {
                            return next.match(copy);
                        }
                    }
                    if (next != null && next.accept(matched.input())) {
                        return next.match(matched.input());
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
            link.visitBranch(visitor, this, state);
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return link.lastState();
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            diagram.edge(diagram.name(state), diagram.name(state));
            diagram.edge(nextName, diagram.name(link.next()));
        }

        @Override
        public String toString() {
            return "(" + state + ")*" + link;
        }
    }

    /** Повторение от единицы: требует своё тело хотя бы раз, а дальше повторяет его жадно. */
    final class OneOrMore implements State {
        private final Link link;
        private final State state;

        OneOrMore(int index, State state) {
            this.link = new Link(index);
            this.state = state;
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
                    Match repeated = matched;
                    while (repeated.ok()) {
                        prev = repeated;
                        repeated = state.match(repeated.input());
                    }
                    State next = link.next();
                    if (next != null && next.accept(prev.input())) {
                        return next.match(prev.input());
                    }
                    return prev;
                }
            }
            return Match.failure(input);
        }

        @Override
        public void visit(Visitor visitor) {
            link.visitBranch(visitor, this, state);
        }

        @Override
        public void setNext(State next) {
            link.setNext(next);
        }

        @Override
        public int index() {
            return link.index();
        }

        @Override
        public State lastState() {
            return link.lastState();
        }

        @Override
        public String diagramLabel() {
            return link.diagramLabel();
        }

        @Override
        public void describe(Diagram diagram, String stateName, String nextName) {
            diagram.edge(nextName, diagram.name(link.next()));
            diagram.edge(diagram.name(state.lastState()), diagram.name(state));
        }

        @Override
        public String toString() {
            return "(" + state + ")+" + link;
        }
    }
}
