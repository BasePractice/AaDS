package ru.mifi.practice.voln.cache.memory;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import ru.mifi.practice.voln.cache.Notifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class NotifiableMemory implements Notifiable, Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, List<Consumer<Long>>> listeners = new ConcurrentHashMap<>();
    private final BlockingQueue<Message> messages;

    public NotifiableMemory(int maximumQueueSize, MeterRegistry registry) {
        this.messages = new LinkedBlockingQueue<>(maximumQueueSize);
        Gauge.builder("CacheNotifyMemory", messages, BlockingQueue::size).tag("target", "queue-size").register(registry);
    }

    @Override
    public void registerNotify(String updateChannel, Consumer<Long> callback) {
        listeners.computeIfAbsent(updateChannel, (u) -> new ArrayList<>()).add(callback);
    }

    @Override
    public void notify(String channel, long key) {
        if (!running.get()) {
            running.set(true);
            executor.execute(this);
        }
        messages.offer(new Message(channel, key));
    }

    @Override
    public void close() throws Exception {
        running.set(false);
        executor.shutdown();
    }

    @Override
    public void run() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                Message message = messages.poll(1000, TimeUnit.MILLISECONDS);
                if (message != null) {
                    List<Consumer<Long>> consumers = listeners.get(message.channel);
                    if (consumers != null) {
                        for (Consumer<Long> consumer : consumers) {
                            consumer.accept(message.key);
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private record Message(String channel, long key) {
    }
}
