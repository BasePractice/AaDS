package ru.mifi.practice.voln.cache;

import ru.mifi.practice.voln.cache.redis.CountRedis;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Main {
    public static void main(String[] args) {
        try (CountRedis balance = new CountRedis("redis://localhost/1", Main::fetchBalance, 5000, 1000)) {
            Count.Value last = null;
            AtomicInteger hint = new AtomicInteger(0);
            for (int i = 1; i <= 1000000000; i++) {
                Optional<Count.Value> value = balance.getValue(1011185);
                if (value.isPresent()) {
                    Count.Value v = value.get();
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
