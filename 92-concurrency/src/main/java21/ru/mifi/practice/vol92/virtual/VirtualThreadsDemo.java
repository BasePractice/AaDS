package ru.mifi.practice.vol92.virtual;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Виртуальные потоки: сто тысяч блокирующихся задач дешевле ста тысяч платформенных потоков.
 * Компилируется с --release 21 и новее, JDK проекта на 17 — см. README модуля.
 */
public final class VirtualThreadsDemo {
    private final AtomicInteger done;

    public VirtualThreadsDemo() {
        this.done = new AtomicInteger();
    }

    public int run(int tasks) throws InterruptedException {
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    TimeUnit.MILLISECONDS.sleep(10);
                    return done.incrementAndGet();
                });
            }
        }
        return done.get();
    }
}
