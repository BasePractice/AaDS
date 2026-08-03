package ru.mifi.practice.voln.cache;

import java.util.Optional;

/** Кэш числового значения по ключу с признаком его актуальности. */
public interface CacheableValue extends AutoCloseable {

    Optional<Value> getValue(long key);

    interface Value {
        long value();

        boolean isActual();
    }
}
