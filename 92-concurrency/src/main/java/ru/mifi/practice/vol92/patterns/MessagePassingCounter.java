package ru.mifi.practice.vol92.patterns;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** Счётчик без общего состояния: потоки передают сообщения через очередь. */
public final class MessagePassingCounter {
    private final BlockingQueue<Integer> inbox;
    private final int stop;

    public MessagePassingCounter(int stop) {
        this.inbox = new LinkedBlockingQueue<>();
        this.stop = stop;
    }

    public void send(int times) throws InterruptedException {
        for (int i = 0; i < times; i++) {
            inbox.put(1);
        }
        inbox.put(stop);
    }

    public int collect(int senders) throws InterruptedException {
        int total = 0;
        int stopped = 0;
        while (stopped < senders) {
            int message = inbox.take();
            if (message == stop) {
                stopped++;
            } else {
                total += message;
            }
        }
        return total;
    }
}
