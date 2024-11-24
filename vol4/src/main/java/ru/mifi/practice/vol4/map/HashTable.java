package ru.mifi.practice.vol4.map;

import ru.mifi.practice.vol4.Counter;

import java.util.Optional;

public interface HashTable<K, V> {

    void put(K key, V value, Counter counter);

    Optional<V> get(K key, Counter counter);

    Optional<V> remove(K key, Counter counter);

    int size();

    final class Entry<K, V> {
        public final K key;
        public final V value;
        private Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Entry<K, V> e = this; e != null; e = e.next) {
                if (i > 0) {
                    sb.append(";");
                }
                sb.append(e.key).append(": ").append(e.value);
                ++i;
            }
            return sb.toString();
        }
    }

    final class Default<K, V> implements HashTable<K, V> {
        private final Entry<K, V>[] entries;
        private int size;

        public Default(int capacity) {
            this.entries = new Entry[capacity];
            this.size = 0;
        }

        public Default() {
            this(256);
        }

        @Override
        public void put(K key, V value, Counter counter) {
            int index = entryIndex(key);
            if (entries[index] == null) {
                entries[index] = new Entry<>(key, value);
                counter.increment();
            } else {
                Entry<K, V> entry = entries[index];
                for (; entry != null; entry = entry.next) {
                    counter.increment();
                    if (entry.next == null) {
                        entry.next = new Entry<>(key, value);
                        break;
                    }
                }
            }
            ++size;
        }

        @Override
        public Optional<V> get(K key, Counter counter) {
            int index = entryIndex(key);
            Entry<K, V> entry = entries[index];
            if (entry == null) {
                counter.increment();
                return Optional.empty();
            }
            for (; entry != null; entry = entry.next) {
                counter.increment();
                if (entry.key.equals(key)) {
                    return Optional.of(entry.value);
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<V> remove(K key, Counter counter) {
            int index = entryIndex(key);
            Entry<K, V> entry = entries[index];
            if (entry == null) {
                counter.increment();
                return Optional.empty();
            }
            for (; entry != null; entry = entry.next) {
                counter.increment();
                if (entry.key.equals(key)) {
                    entry.next = entry.next.next;
                    size--;
                }
            }
            return Optional.empty();
        }

        @Override
        public int size() {
            return size;
        }

        private int entryIndex(K key) {
            return Math.abs(key.hashCode() % entries.length);
        }

        @SuppressWarnings("PMD.ForLoopCanBeForeach")
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < entries.length; ++i) {
                Entry<K, V> entry = entries[i];
                if (entry != null) {
                    sb.append(entry).append("\n");
                }
            }
            return sb.toString();
        }
    }
}
