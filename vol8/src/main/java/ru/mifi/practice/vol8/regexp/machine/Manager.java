package ru.mifi.practice.vol8.regexp.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/** Фабрика и реестр состояний конечного автомата. */
public interface Manager {
    State.Symbol symbol(char symbol);

    State.Sequence sequence();

    State.Group group();

    State.Parallel parallel();

    State.Any any();

    State.Excluding excluding(State excluded);

    State.NoneOrOne noneOrOne(State state);

    State.NoneOrMore noneOrMore(State state);

    State.OneOrMore oneOrMore(State state);

    Optional<State> of(int index);

    void reset();

    @FunctionalInterface
    interface CharacterMapper<T> {
        T map(Character c);
    }

    final class Default implements Manager {
        private final AtomicInteger counter = new AtomicInteger();
        private final List<State> states = new ArrayList<>();
        private final CharacterMapper<?> characterMapper;

        public Default(CharacterMapper<?> characterMapper) {
            this.characterMapper = characterMapper;
        }

        public Default() {
            this(c -> c);
        }

        @Override
        public State.Symbol symbol(char symbol) {
            return register(new State.Symbol(this, counter.getAndIncrement(), map(symbol)));
        }

        @Override
        public State.Sequence sequence() {
            return register(new State.Sequence(this, counter.getAndIncrement()));
        }

        @Override
        public State.Group group() {
            return register(new State.Group(this, counter.getAndIncrement()));
        }

        @Override
        public State.Parallel parallel() {
            return register(new State.Parallel(this, counter.getAndIncrement()));
        }

        @Override
        public State.Any any() {
            return register(new State.Any(this, counter.getAndIncrement()));
        }

        @Override
        public State.Excluding excluding(State excluded) {
            return register(new State.Excluding(this, counter.getAndIncrement(), excluded));
        }

        @Override
        public State.NoneOrOne noneOrOne(State state) {
            return register(new State.NoneOrOne(this, counter.getAndIncrement(), state));
        }

        @Override
        public State.NoneOrMore noneOrMore(State state) {
            return register(new State.NoneOrMore(this, counter.getAndIncrement(), state));
        }

        @Override
        public State.OneOrMore oneOrMore(State state) {
            return register(new State.OneOrMore(this, counter.getAndIncrement(), state));
        }

        @Override
        public Optional<State> of(int index) {
            if (index < 0 || index >= states.size()) {
                return Optional.empty();
            }
            return Optional.of(states.get(index));
        }

        private <S extends State> S register(S state) {
            states.add(state);
            return state;
        }

        @SuppressWarnings("unchecked")
        private <O> O map(Character c) {
            return (O) characterMapper.map(c);
        }

        @Override
        public void reset() {
            counter.set(0);
            states.clear();
        }
    }
}
