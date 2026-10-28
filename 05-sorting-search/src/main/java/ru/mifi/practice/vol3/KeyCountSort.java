package ru.mifi.practice.vol3;

import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Заготовка к заданию слайда 259: сортировка подсчётом объектов по целому ключу 0..k−1. В отличие
 * от CountSort, который пересчитывает сами числа, здесь переставляются объекты, поэтому важна
 * устойчивость: объекты с одинаковым ключом должны остаться в исходном порядке. Для этого
 * префиксные суммы обходят вход справа налево.
 */
public interface KeyCountSort<E> {

    List<E> sort(List<E> items, ToIntFunction<E> key, int keys);

    /**
     * Место для решения студента.
     */
    final class Stable<E> implements KeyCountSort<E> {

        @Override
        public List<E> sort(List<E> items, ToIntFunction<E> key, int keys) {
            throw new UnsupportedOperationException("Задание слайда 259 ещё не выполнено");
        }
    }
}
