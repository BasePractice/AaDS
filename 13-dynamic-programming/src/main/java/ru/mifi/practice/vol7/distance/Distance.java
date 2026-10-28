package ru.mifi.practice.vol7.distance;

import ru.mifi.practice.commons.Counter;

/** Редакционное расстояние между двумя строками. */
public interface Distance {
    int distance(String s1, String s2, Counter counter);
}
