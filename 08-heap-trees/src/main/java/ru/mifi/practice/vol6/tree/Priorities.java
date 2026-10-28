package ru.mifi.practice.vol6.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Куча минимума, в которой ключ адресуется по имени, а не по позиции: приоритет уже лежащего в
 * куче ключа можно уменьшить, и элемент сам всплывёт на своё место. Обычной куче такое не по
 * силам — найти в ней элемент стоит O(n), поэтому Дейкстра на ней либо копит устаревшие копии
 * вершин, либо перебирает непосещённые.
 *
 * <p>Позиция каждого ключа держится в отдельной карте и обновляется при каждом обмене, поэтому
 * уменьшение стоит O(log n), а весь поиск кратчайших путей — O(m log n).
 */
public interface Priorities<K, P extends Comparable<P>> {

    Priorities<K, P> add(K key, P priority);

    void decrease(K key, P priority);

    K poll();

    boolean holds(K key);

    int size();

    /** Двоичная куча: ключи лежат массивом, родитель ключа с позицией i — на позиции (i−1)/2. */
    final class Binary<K, P extends Comparable<P>> implements Priorities<K, P> {
        private final List<K> keys = new ArrayList<>();
        private final Map<K, P> priorities = new HashMap<>();
        private final Map<K, Integer> positions = new HashMap<>();

        @Override
        public Priorities<K, P> add(K key, P priority) {
            if (holds(key)) {
                throw new IllegalArgumentException("Ключ " + key + " уже лежит в куче");
            }
            keys.add(key);
            priorities.put(key, priority);
            positions.put(key, keys.size() - 1);
            raise(keys.size() - 1);
            return this;
        }

        @Override
        public void decrease(K key, P priority) {
            if (!holds(key)) {
                throw new NoSuchElementException("Ключа " + key + " в куче нет, уменьшать нечего");
            }
            P known = priorities.get(key);
            if (priority.compareTo(known) > 0) {
                throw new IllegalArgumentException(
                    "Приоритет ключа " + key + " только уменьшается, а " + priority + " больше " + known);
            }
            priorities.put(key, priority);
            raise(positions.get(key));
        }

        @Override
        public K poll() {
            if (keys.isEmpty()) {
                throw new NoSuchElementException("Куча пуста");
            }
            K top = keys.get(0);
            swap(0, keys.size() - 1);
            keys.remove(keys.size() - 1);
            positions.remove(top);
            priorities.remove(top);
            if (!keys.isEmpty()) {
                sink(0);
            }
            return top;
        }

        @Override
        public boolean holds(K key) {
            return positions.containsKey(key);
        }

        @Override
        public int size() {
            return keys.size();
        }

        private void raise(int position) {
            int current = position;
            while (current > 0 && smaller(current, parent(current))) {
                swap(current, parent(current));
                current = parent(current);
            }
        }

        private void sink(int position) {
            int current = position;
            while (true) {
                int smallest = smallest(current);
                if (smallest == current) {
                    return;
                }
                swap(current, smallest);
                current = smallest;
            }
        }

        private int smallest(int position) {
            int smallest = position;
            for (int child : List.of(position * 2 + 1, position * 2 + 2)) {
                if (child < keys.size() && smaller(child, smallest)) {
                    smallest = child;
                }
            }
            return smallest;
        }

        private int parent(int position) {
            return (position - 1) / 2;
        }

        private boolean smaller(int left, int right) {
            return priorities.get(keys.get(left)).compareTo(priorities.get(keys.get(right))) < 0;
        }

        private void swap(int left, int right) {
            K carry = keys.get(left);
            keys.set(left, keys.get(right));
            keys.set(right, carry);
            positions.put(keys.get(left), left);
            positions.put(keys.get(right), right);
        }

        @Override
        public String toString() {
            return keys.toString();
        }
    }
}
