package ru.mifi.practice.vol8.regexp.machine;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class State {
    final Manager manager;
    final int index;
    private final String gName;
    protected boolean done;

    protected State(Manager manager, int index) {
        this.manager = manager;
        this.gName = prefix(this.getClass());
        this.index = index;
    }

    private static String prefix(Class<?> stateClass) {
        String name = stateClass.getSimpleName();
        if (name.contains("Bridge")) {
            return "BR";
        } else if (name.contains("Epsilon")) {
            return "EP";
        } else if (name.contains("Parallel")) {
            return "PL";
        } else if (name.contains("Sequence")) {
            return "SQ";
        } else if (name.contains("Symbol")) {
            return "SY";
        }
        return "ST";
    }

    public void reset() {
        //Nothing
    }

    public boolean accept(Input input) {
        return false;
    }

    boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return String.format("%s%02d", gName, index);
    }

    public interface Manager {
        <S extends State> S newState(Class<S> stateClass, Object... args);

        Optional<State> of(int index);

        @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
        final class Default implements Manager {
            private final AtomicInteger counter = new AtomicInteger();
            private final List<State> states = new ArrayList<>();

            @SneakyThrows
            @Override
            public <S extends State> S newState(Class<S> stateClass, Object... args) {
                List<Class<?>> paramsClass = new ArrayList<>();
                paramsClass.add(Manager.class);
                paramsClass.add(int.class);
                for (Object arg : args) {
                    paramsClass.add(arg.getClass());
                }
                Constructor<S> constructor = stateClass.getDeclaredConstructor(paramsClass.toArray(new Class[0]));
                constructor.setAccessible(true);
                List<Object> params = new ArrayList<>();
                params.add(this);
                params.add(counter.getAndIncrement());
                Collections.addAll(params, args);
                S newed = constructor.newInstance(params.toArray(new Object[0]));
                states.add(newed);
                return newed;
            }

            @Override
            public Optional<State> of(int index) {
                if (index < 0 || index >= states.size()) {
                    return Optional.empty();
                }
                return Optional.of(states.get(index));
            }
        }
    }

    static final class Symbol extends State {
        private final char symbol;

        private Symbol(Manager manager, int index, Character symbol) {
            super(manager, index);
            this.symbol = symbol;
        }

        @Override
        public boolean accept(Input input) {
            return input.peek().map(c -> c == symbol).orElse(false);
        }
    }

    static final class Epsilon extends State {
        private Epsilon(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public boolean accept(Input input) {
            return true;
        }
    }

    static final class Bridge extends State {
        private final int start;
        private final int end;
        private State current;

        private Bridge(Manager manager, int index, Integer start, Integer end) {
            super(manager, index);
            this.start = start;
            this.end = end;
        }

        @Override
        public void reset() {
            current = null;
        }

        @Override
        boolean isDone() {
            return getCurrent().isDone();
        }

        @Override
        public boolean accept(Input input) {
            return getCurrent().accept(input);
        }

        private State getCurrent() {
            if (current == null) {
                current = manager.of(start).orElse(null);
            }
            Objects.requireNonNull(current);
            if (current.isDone()) {
                current = manager.of(end).orElse(null);
            }
            Objects.requireNonNull(current);
            return current;
        }

        @Override
        public String toString() {
            return super.toString() + "("
                + manager.of(start).map(State::toString).orElse("") + "->"
                + manager.of(end).map(State::toString).orElse("") + ")";
        }
    }

    static final class Sequence extends State {
        private final List<Integer> states = new ArrayList<>();
        private int index;
        private State current;

        private Sequence(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public void reset() {
            index = 0;
        }

        @SuppressWarnings("PMD.SimplifyBooleanReturns")
        @Override
        public boolean accept(Input input) {
            if (index >= states.size()) {
                return false;
            }
            return getCurrent().accept(input);
        }

        @Override
        boolean isDone() {
            return getCurrent().isDone();
        }

        private State getCurrent() {
            if (current == null) {
                current = manager.of(states.get(index)).orElse(null);
            }
            Objects.requireNonNull(current);
            if (current.isDone()) {
                index++;
                return getCurrent();
            }
            return current;
        }

        void add(int state) {
            states.add(state);
        }
    }

    static final class Parallel extends State {
        private final List<Integer> states = new ArrayList<>();
        private final List<State> current = new ArrayList<>();

        private Parallel(Manager manager, int index) {
            super(manager, index);
        }

        @Override
        public void reset() {
            current.clear();
            states.forEach(i -> {
                current.add(manager.of(i).orElse(null));
            });
        }

        @Override
        public boolean accept(Input input) {
            Iterator<State> it = current.iterator();
            while (it.hasNext()) {
                State state = it.next();
                if (state.isDone()) {
                    it.remove();
                } else if (!state.accept(input)) {
                    it.remove();
                }
            }
            return !current.isEmpty();
        }

        @Override
        boolean isDone() {
            return current.stream().map(State::isDone).reduce(true, (a, b) -> a && b);
        }

        void add(int state) {
            states.add(state);
        }
    }
}
