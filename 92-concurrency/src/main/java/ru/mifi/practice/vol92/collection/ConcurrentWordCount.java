package ru.mifi.practice.vol92.collection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Подсчёт слов в ConcurrentHashMap: merge атомарно обновляет ячейку. */
public final class ConcurrentWordCount {
    private final Map<String, Integer> counts;

    public ConcurrentWordCount() {
        this.counts = new ConcurrentHashMap<>();
    }

    public void count(List<String> words) {
        for (String word : words) {
            counts.merge(word, 1, Integer::sum);
        }
    }

    public int frequency(String word) {
        return counts.getOrDefault(word, 0);
    }
}
