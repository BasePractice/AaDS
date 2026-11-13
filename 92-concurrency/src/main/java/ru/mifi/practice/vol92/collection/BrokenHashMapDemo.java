package ru.mifi.practice.vol92.collection;

import java.util.Map;

/** Демонстрация гонки в HashMap: рост таблицы из двух потоков теряет записи. */
public final class BrokenHashMapDemo implements Runnable {
    private final Map<Integer, Integer> map;
    private final int from;
    private final int to;

    public BrokenHashMapDemo(Map<Integer, Integer> map, int from, int to) {
        this.map = map;
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        for (int i = from; i < to; i++) {
            map.put(i, i);
        }
    }
}
