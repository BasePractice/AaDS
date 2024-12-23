package ru.mifi.practice.vol8.regexp.machine;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public interface Manager {
    <S extends State> S newState(Class<S> stateClass, Object... args);

    Optional<State> of(int index);

    void reset();

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
                Class<?> aClass = arg.getClass();
                if (arg instanceof State) {
                    paramsClass.add(State.class);
                } else {
                    paramsClass.add(aClass);
                }
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

        @Override
        public void reset() {
            counter.set(0);
            states.clear();
        }
    }
}
