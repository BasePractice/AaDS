package ru.mifi.practice.voln.trampoline;

import java.util.function.Supplier;

/** Трамплин для разворачивания рекурсии в цикл без переполнения стека. */
public interface Trampoline<T> {
    static <T> Trampoline<T> done(T value) {
        return new Done<>(value);
    }

    static <T> Trampoline<T> more(Supplier<Trampoline<T>> next) {
        return new More<>(next);
    }

    T get();

    default Trampoline<T> run() {
        return this;
    }

    record Done<T>(T value) implements Trampoline<T> {
        @Override
        public T get() {
            return value;
        }
    }

    record More<T>(Supplier<Trampoline<T>> next) implements Trampoline<T> {

        @Override
        public Trampoline<T> run() {
            return next.get();
        }

        @Override
        public T get() {
            throw new UnsupportedOperationException();
        }
    }
}
