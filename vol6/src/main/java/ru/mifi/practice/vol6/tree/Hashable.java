package ru.mifi.practice.vol6.tree;

/** Объект, отдающий структурный хеш самого себя. */
@FunctionalInterface
public interface Hashable {
    int hash();
}
