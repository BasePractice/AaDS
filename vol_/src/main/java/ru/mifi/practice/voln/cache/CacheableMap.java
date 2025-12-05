package ru.mifi.practice.voln.cache;

import java.util.Map;

public interface CacheableMap extends AutoCloseable {
    void hSet(String key, Map<String, Long> values);

    Map<String, Long> hGet(String key);
}
