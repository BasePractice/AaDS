package ru.mifi.practice.vol8.regexp.machine;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public interface Manager {
    <S extends State> S newState(Class<S> stateClass, Object... args);

    Optional<State> of(int index);

    void reset();

    @FunctionalInterface
    interface CharacterMapper<T> {
        T map(Character c);
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
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
        public <S extends State> S newState(Class<S> stateClass, Object... args) {
            try {
                List<Class<?>> paramsClass = new ArrayList<>();
                paramsClass.add(Manager.class);
                paramsClass.add(int.class);
                for (Object arg : args) {
                    Class<?> aClass = arg.getClass();
                    if (arg instanceof State) {
                        paramsClass.add(State.class);
                    } else if (arg instanceof Character) {
                        paramsClass.add(Object.class);
                    } else {
                        paramsClass.add(aClass);
                    }
                }
                Constructor<S> constructor = stateClass.getDeclaredConstructor(paramsClass.toArray(new Class[0]));
                constructor.setAccessible(true);
                List<Object> params = new ArrayList<>();
                params.add(this);
                params.add(counter.getAndIncrement());
                for (Object arg : args) {
                    if (arg instanceof Character ch) {
                        params.add(map(ch));
                    } else {
                        params.add(arg);
                    }
                }
                S newed = constructor.newInstance(params.toArray(new Object[0]));
                states.add(newed);
                return newed;
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException("Cannot create state instance for " + stateClass, ex);
            }
        }

        @Override
        public Optional<State> of(int index) {
            if (index < 0 || index >= states.size()) {
                return Optional.empty();
            }
            return Optional.of(states.get(index));
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
