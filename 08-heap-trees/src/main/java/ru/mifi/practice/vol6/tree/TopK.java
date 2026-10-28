package ru.mifi.practice.vol6.tree;

import java.util.Iterator;
import java.util.List;

/**
 * Заготовка к заданию слайда 476: k самых больших чисел потока без хранения всего потока. Держим
 * кучу из k лучших на данный момент. Новое число сравнивается с худшим из них, то есть с
 * наименьшим, — поэтому куча нужна min-heap, а не max-heap: её корень и есть кандидат на вылет.
 * Память O(k), время O(n log k).
 */
public interface TopK {

    /**
     * Самые большие числа потока по убыванию.
     */
    List<Integer> largest(Iterator<Integer> stream, int k);

    /**
     * Место для решения студента.
     */
    final class Bounded implements TopK {

        @Override
        public List<Integer> largest(Iterator<Integer> stream, int k) {
            throw new UnsupportedOperationException("Задание слайда 476 ещё не выполнено");
        }
    }
}
