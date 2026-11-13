package ru.mifi.practice.vol92.virtual;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * По одному виртуальному потоку на каждую задачу с ожиданием.
 * Компилируется с --release 21, JDK проекта на 17 — см. README модуля.
 */
public final class VirtualThreadsDemo {
    public static void main(String[] args) throws InterruptedException {
        int tasks = args.length == 0 ? 100_000 : Integer.parseInt(args[0]);
        int completed = new VirtualThreadsDemo().run(tasks);
        System.out.printf("Завершено задач: %d из %d%n", completed, tasks);
    }

    public int run(int tasks) throws InterruptedException {
        AtomicInteger done = new AtomicInteger();
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    TimeUnit.MILLISECONDS.sleep(10);
                    return done.incrementAndGet();
                });
            }
        }
        if (Thread.interrupted()) {
            throw new InterruptedException("Ожидание завершения виртуальных потоков прервано");
        }
        return done.get();
    }
}
