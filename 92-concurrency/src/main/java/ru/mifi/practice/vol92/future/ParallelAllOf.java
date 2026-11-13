package ru.mifi.practice.vol92.future;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/** Параллельные задачи и allOf: ждём все результаты сразу. */
public final class ParallelAllOf {
    public long total(int tasks) {
        CompletableFuture<Long>[] futures = IntStream.range(0, tasks)
            .mapToObj(task -> CompletableFuture.supplyAsync(() -> {
                long total = 0;
                for (int i = 1; i <= task * 100; i++) {
                    total += i;
                }
                return total;
            }))
            .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        long total = 0;
        for (CompletableFuture<Long> future : futures) {
            total += future.join();
        }
        return total;
    }
}
