package ru.mifi.practice.vol4.map;

import ru.mifi.practice.vol4.Counter;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public interface SetTable<K> extends HashTable<K, Boolean> {

    default void add(K key, Counter counter) {
        put(key, Boolean.TRUE, counter);
    }

    default boolean contains(K key, Counter counter) {
        return get(key, counter).isPresent();
    }

    default boolean delete(K key, Counter counter) {
        return remove(key, counter).isPresent();
    }

    final class Default<K> implements SetTable<K> {
        private final HashTable<K, Boolean> table;

        public Default(HashTable<K, Boolean> map) {
            this.table = map;
        }

        public Default(int capacity) {
            this(new HashTable.Default<>(capacity));
        }

        @Override
        public void put(K key, Boolean value, Counter counter) {
            table.put(key, value, counter);
        }

        @Override
        public Optional<Boolean> get(K key, Counter counter) {
            return table.get(key, counter);
        }

        @Override
        public Optional<Boolean> remove(K key, Counter counter) {
            return table.remove(key, counter);
        }

        @Override
        public int size() {
            return table.size();
        }

        @Override
        public void clear() {
            table.clear();
        }

        @Override
        public void print() {
            table.print();
        }

        @Override
        public Stream<Entry<K, Boolean>> sorted(Comparator<Entry<K, Boolean>> comparator) {
            return table.sorted(comparator);
        }
    }
}
