package ru.mifi.practice.vol92.sync;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/** Стартовый затвор: потоки ждут щелчка и стартуют одновременно. */
public final class LatchedStart {
    private final CountDownLatch latch;
    private final AtomicInteger started;

    public LatchedStart() {
        this.latch = new CountDownLatch(1);
        this.started = new AtomicInteger();
    }

    public void await() throws InterruptedException {
        latch.await();
        started.incrementAndGet();
    }

    public void click() {
        latch.countDown();
    }

    public int started() {
        return started.get();
    }
}
