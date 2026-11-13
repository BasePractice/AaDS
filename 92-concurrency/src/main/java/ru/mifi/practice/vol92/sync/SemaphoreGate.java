package ru.mifi.practice.vol92.sync;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/** Семофор на permits пропусков: в комнате не больше permits потоков сразу. */
public final class SemaphoreGate {
    private final Semaphore permits;
    private final AtomicInteger inside;
    private final AtomicInteger peak;

    public SemaphoreGate(int permits) {
        this.permits = new Semaphore(permits);
        this.inside = new AtomicInteger();
        this.peak = new AtomicInteger();
    }

    public void enter() throws InterruptedException {
        permits.acquire();
        int now = inside.incrementAndGet();
        peak.accumulateAndGet(now, Math::max);
    }

    public void leave() {
        inside.decrementAndGet();
        permits.release();
    }

    public int peak() {
        return peak.get();
    }
}
