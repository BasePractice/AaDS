package ru.mifi.practice.vol92.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Место для решения студента: перекачайте items сообщений
 * из очереди производителя в список через take.
 */
public final class TaskProducerConsumer {

    public List<String> pipe(ProducerConsumer queue, int items) {
        throw new UnsupportedOperationException("Место для решения студента: " + items + " сообщений");
    }

    public List<String> empty() {
        return new ArrayList<>(0);
    }
}
