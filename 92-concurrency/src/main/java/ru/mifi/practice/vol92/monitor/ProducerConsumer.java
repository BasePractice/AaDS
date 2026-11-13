package ru.mifi.practice.vol92.monitor;

/** Кольцевой буфер на мониторе: put и take ждут на wait/notifyAll. */
public final class ProducerConsumer {
    private final String[] slots;
    private int head;
    private int tail;
    private int size;

    public ProducerConsumer(int capacity) {
        this.slots = new String[capacity];
    }

    public synchronized void put(String item) throws InterruptedException {
        while (size == slots.length) {
            wait();
        }
        slots[tail] = item;
        tail = (tail + 1) % slots.length;
        size++;
        notifyAll();
    }

    public synchronized String take() throws InterruptedException {
        while (size == 0) {
            wait();
        }
        final String item = slots[head];
        head = (head + 1) % slots.length;
        size--;
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return size;
    }
}
