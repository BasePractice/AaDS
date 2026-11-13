package ru.mifi.practice.vol92.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/** Комната с tryLock: поток не ждёт занятую дверь, а разворачивается. */
public final class TryLockRoom {
    private final ReentrantLock door;
    private final AtomicInteger visits;
    private final AtomicInteger denials;

    public TryLockRoom() {
        this.door = new ReentrantLock();
        this.visits = new AtomicInteger();
        this.denials = new AtomicInteger();
    }

    public boolean visit(long hold, TimeUnit unit) {
        if (!door.tryLock()) {
            denials.incrementAndGet();
            return false;
        }
        try {
            unit.sleep(hold);
            visits.incrementAndGet();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            door.unlock();
        }
    }

    public int visits() {
        return visits.get();
    }

    public int denials() {
        return denials.get();
    }
}
