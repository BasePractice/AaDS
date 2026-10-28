package ru.mifi.practice.vol8.regexp.machine;

/**
 * Обходчик состояний конечного автомата. Каждый метод по умолчанию ничего не делает, поэтому
 * реализация описывает только интересные ей события обхода.
 */
public interface Visitor {

    default void start() {
        //Nothing
    }

    default void end() {
        //Nothing
    }

    default void visit(State from, State state) {
        //Nothing
    }
}
