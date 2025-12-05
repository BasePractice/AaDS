package ru.mifi.practice.voln.cache;

import java.util.Optional;

public interface Count extends AutoCloseable {

    Optional<Value> getValue(long userId);

    interface Value {
        long value();

        boolean isActual();
    }
}
