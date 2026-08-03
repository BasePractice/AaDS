package ru.mifi.practice.voln.cache;

import java.util.Map;

/** Кэш хешей, сопоставляющий ключу набор именованных числовых полей. */
public interface CacheableMap extends AutoCloseable {
    void hSet(String key, Map<String, Long> values);

    Map<String, Long> hGet(String key);
}
