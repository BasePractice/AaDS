package ru.mifi.practice.vol92.atomic;

import java.util.concurrent.atomic.LongAdder;

/** LongAdder для статистики: ячейки складываются только при чтении. */
public final class AdderStats {
    private final LongAdder total;

    public AdderStats() {
        this.total = new LongAdder();
    }

    public void track(int times) {
        for (int i = 0; i < times; i++) {
            total.increment();
        }
    }

    public long total() {
        return total.sum();
    }
}
