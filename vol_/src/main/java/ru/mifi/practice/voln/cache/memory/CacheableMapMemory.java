package ru.mifi.practice.voln.cache.memory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.GuavaCacheMetrics;
import ru.mifi.practice.voln.cache.CacheableMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class CacheableMapMemory implements CacheableMap {
    private final Cache<String, Map<String, Long>> values;

    public CacheableMapMemory(MeterRegistry registry) {
        this.values = CacheBuilder.newBuilder()
            .expireAfterWrite(100000, TimeUnit.MILLISECONDS)
            .recordStats()
            .maximumSize(10000000)
            .build();
        GuavaCacheMetrics.monitor(registry, values, "CacheMapMemory");
    }

    @Override
    public void hSet(String key, Map<String, Long> values) {
        this.values.put(key, values);
    }

    @Override
    public Map<String, Long> hGet(String key) {
        return this.values.getIfPresent(key);
    }

    @Override
    public void close() {
        values.invalidateAll();
    }
}
