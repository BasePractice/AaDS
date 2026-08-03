package ru.mifi.practice.vol5.graph;

/** Rule that combines two edge weights into a single accumulated weight. */
@FunctionalInterface
public interface Weight<W extends Number & Comparable<W>> {
    static Weight<Integer> ofInteger() {
        return Integer::sum;
    }

    W sum(W v1, W v2);
}
