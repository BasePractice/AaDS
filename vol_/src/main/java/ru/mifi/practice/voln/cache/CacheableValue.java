package ru.mifi.practice.voln.cache;

import java.util.Optional;

public interface CacheableValue extends AutoCloseable {

    Optional<Value> getValue(long key);

    interface Value {
        long value();

        boolean isActual();
    }
}
