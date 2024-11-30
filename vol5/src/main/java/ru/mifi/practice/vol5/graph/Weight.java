package ru.mifi.practice.vol5.graph;

public interface Weight<W extends Number & Comparable<W>> {
    W sum(W v1, W v2);

    static Weight<Integer> ofInteger() {
        return Integer::sum;
    }
}
