package ru.mifi.practice.voln.cache;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import ru.mifi.practice.voln.cache.memory.CacheableMapMemory;
import ru.mifi.practice.voln.cache.memory.NotifiableMemory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Main {
    public static void main(String[] args) throws Exception {
        MeterRegistry registry = new SimpleMeterRegistry();
//        CacheMap map = new CacheMapRedis("redis://localhost/1");
        CacheableMap map = new CacheableMapMemory(registry);
        try (Notifiable notify = new NotifiableMemory(10000, registry);
             SimpleCacheableValue balance = new SimpleCacheableValue(map, notify, Main::fetchBalance, 1000, 1000)) {
            CacheableValue.Value last = null;
            AtomicInteger hint = new AtomicInteger(0);
            for (int i = 1; i <= 1000000000; i++) {
                Optional<CacheableValue.Value> value = balance.getValue(1011185);
                if (value.isPresent()) {
                    CacheableValue.Value v = value.get();
                    if (last == null) {
                        last = v;
                        System.out.printf("[%9d,%9d] %s%n", 0, balance.lastCacheHits(), v);
                    } else if (!last.equals(v)) {
                        last = v;
                        System.out.printf("[%9d,%9d] %s%n", hint.intValue(), balance.lastCacheHits(), v);
                        hint.set(0);
                    } else {
                        hint.incrementAndGet();
                    }
                } else {
                    System.err.println("Value not found");
                }
            }
        }
        System.out.println("Завершили");
    }

    private static long fetchBalance(long userId) {
        return userId + 30000L;
    }
}
