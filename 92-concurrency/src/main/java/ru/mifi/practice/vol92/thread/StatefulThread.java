package ru.mifi.practice.vol92.thread;

/** Обёртка над потоком: состояния NEW, RUNNABLE и TERMINATED видно снаружи. */
public final class StatefulThread {
    private final Thread thread;

    public StatefulThread(Runnable task) {
        this.thread = new Thread(task);
    }

    public Thread.State state() {
        return thread.getState();
    }

    public void start() {
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }
}
