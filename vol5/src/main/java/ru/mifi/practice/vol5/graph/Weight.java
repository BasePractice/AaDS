package ru.mifi.practice.vol5.graph;

/** Правило свёртки двух весов рёбер в один накопленный вес. */
@FunctionalInterface
public interface Weight<W extends Number & Comparable<W>> {
    static Weight<Integer> ofInteger() {
        return Integer::sum;
    }

    W sum(W v1, W v2);
}
