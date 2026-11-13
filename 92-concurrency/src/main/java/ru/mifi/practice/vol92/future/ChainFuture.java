package ru.mifi.practice.vol92.future;

import java.util.concurrent.CompletableFuture;

/** Цепочка CompletableFuture: шаги без вложенных колбэков и get. */
public final class ChainFuture {
    public CompletableFuture<String> pipeline(String source) {
        return CompletableFuture.supplyAsync(source::trim)
            .thenApply(String::toLowerCase)
            .thenApply(text -> text.replace(' ', '-'));
    }
}
