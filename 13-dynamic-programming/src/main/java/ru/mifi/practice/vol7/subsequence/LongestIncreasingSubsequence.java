package ru.mifi.practice.vol7.subsequence;

import ru.mifi.practice.commons.Counter;

import java.util.List;

/**
 * Длина наибольшей строго возрастающей подпоследовательности. Классическая задача раздела 14:
 * элементы выбираются в исходном порядке, но не подряд, поэтому жадный проход не годится —
 * взятый малый элемент может оказаться дороже пропущенного большого.
 */
@FunctionalInterface
public interface LongestIncreasingSubsequence {

    int longest(List<Integer> values, Counter counter);

    /**
     * Перебор: для каждого элемента решается, брать его или пропустить, и обе ветки считаются
     * до конца. Дерево решений полное, поэтому цена O(2^n) — на трёх десятках элементов задача
     * уже не считается, и именно поэтому её решают таблицей.
     */
    final class Recursion implements LongestIncreasingSubsequence {

        @Override
        public int longest(List<Integer> values, Counter counter) {
            return longest(values, 0, Integer.MIN_VALUE, counter);
        }

        private int longest(List<Integer> values, int index, int previous, Counter counter) {
            counter.increment();
            if (index == values.size()) {
                return 0;
            }
            int skipped = longest(values, index + 1, previous, counter);
            if (values.get(index) <= previous) {
                return skipped;
            }
            return Math.max(skipped, 1 + longest(values, index + 1, values.get(index), counter));
        }
    }

    /**
     * Таблица: в ячейке i лежит длина наибольшей возрастающей подпоследовательности, кончающейся
     * на элементе i. Каждая ячейка смотрит на все левые и берёт лучшую, к которой можно
     * пристроиться, — O(n²) вместо перебора, а ответ собирается максимумом по таблице.
     */
    final class Dynamited implements LongestIncreasingSubsequence {

        @Override
        public int longest(List<Integer> values, Counter counter) {
            int[] lengths = new int[values.size()];
            int longest = 0;
            for (int i = 0; i < values.size(); i++) {
                lengths[i] = 1;
                for (int j = 0; j < i; j++) {
                    counter.increment();
                    if (values.get(j) < values.get(i) && lengths[j] + 1 > lengths[i]) {
                        lengths[i] = lengths[j] + 1;
                    }
                }
                longest = Math.max(longest, lengths[i]);
            }
            return longest;
        }
    }
}
