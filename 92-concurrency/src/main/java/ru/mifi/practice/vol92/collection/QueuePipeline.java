package ru.mifi.practice.vol92.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Конвейер из двух потоков через ArrayBlockingQueue со стоп-значением. */
public final class QueuePipeline {
    private final BlockingQueue<Integer> queue;
    private final int stop;

    public QueuePipeline(int capacity, int stop) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.stop = stop;
    }

    public void stage(int items) throws InterruptedException {
        for (int i = 0; i < items; i++) {
            queue.put(i);
        }
        queue.put(stop);
    }

    public List<Integer> drain() throws InterruptedException {
        List<Integer> taken = new ArrayList<>();
        int item;
        while ((item = queue.take()) != stop) {
            taken.add(item);
        }
        return taken;
    }
}
