package ru.mifi.practice.vol92.thread;

import java.util.List;

/** Задача, которая останавливается по interrupt, а не по проверке чужого флага. */
public final class InterruptibleTask implements Runnable {
    private final List<String> journal;

    public InterruptibleTask(List<String> journal) {
        this.journal = journal;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        journal.add("остановлен");
    }
}
