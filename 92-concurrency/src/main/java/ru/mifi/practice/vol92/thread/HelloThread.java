package ru.mifi.practice.vol92.thread;

import java.util.List;

/** Поток, пишущий свои шаги в общий журнал: start против run, join для ожидания. */
public final class HelloThread implements Runnable {
    private final String name;
    private final int steps;
    private final List<String> journal;

    public HelloThread(String name, int steps, List<String> journal) {
        this.name = name;
        this.steps = steps;
        this.journal = journal;
    }

    @Override
    public void run() {
        for (int i = 0; i < steps; i++) {
            journal.add(name + " шаг " + i);
        }
    }
}
