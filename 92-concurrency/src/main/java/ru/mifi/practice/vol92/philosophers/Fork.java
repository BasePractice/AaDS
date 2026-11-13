package ru.mifi.practice.vol92.philosophers;

import java.util.concurrent.locks.ReentrantLock;

/** Вилка на столе: одна на ReentrantLock. */
public final class Fork {
    private final ReentrantLock lock;
    private final int index;

    public Fork(int index) {
        this.lock = new ReentrantLock();
        this.index = index;
    }

    public void grab() throws InterruptedException {
        lock.lockInterruptibly();
    }

    public boolean take() {
        return lock.tryLock();
    }

    public void put() {
        lock.unlock();
    }

    public int index() {
        return index;
    }
}
