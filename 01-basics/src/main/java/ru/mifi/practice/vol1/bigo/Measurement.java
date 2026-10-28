package ru.mifi.practice.vol1.bigo;

import ru.mifi.practice.commons.Counter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Замер числа шагов одной нагрузки на растущих входах. Ответ — таблица «n → шагов», по которой
 * класс сложности виден без секундомера: постоянная нагрузка стоит на месте, линейная удваивается
 * вместе с входом, квадратичная вырастает вчетверо.
 */
public final class Measurement {
    private final Workload workload;
    private final List<Integer> sizes;

    public Measurement(Workload workload, int... sizes) {
        this(workload, Arrays.stream(sizes).boxed().toList());
    }

    public Measurement(Workload workload, List<Integer> sizes) {
        this.workload = workload;
        this.sizes = List.copyOf(sizes);
    }

    public Map<Integer, Integer> steps() {
        Map<Integer, Integer> steps = new LinkedHashMap<>();
        for (int size : sizes) {
            Counter counter = Counter.create();
            workload.run(size, counter);
            steps.put(size, counter.count());
        }
        return steps;
    }

    @Override
    public String toString() {
        StringBuilder table = new StringBuilder(String.format("%8s | %8s%n", "n", "шагов"));
        for (Map.Entry<Integer, Integer> row : steps().entrySet()) {
            table.append(String.format("%8d | %8d%n", row.getKey(), row.getValue()));
        }
        return table.toString();
    }
}
