package ru.mifi.practice.vol5.graph;

public abstract class Factory {
    public static Graph<Void, Integer> createGraph() {
        return new StandardGraph<>();
    }
}
