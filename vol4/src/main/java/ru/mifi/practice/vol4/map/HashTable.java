package ru.mifi.practice.vol4.map;

import ru.mifi.practice.vol4.Counter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface HashTable<K, V> {

    void put(K key, V value, Counter counter);

    Optional<V> get(K key, Counter counter);

    Optional<V> remove(K key, Counter counter);

    int size();

    void clear();

    void print();

    default boolean isEmpty() {
        return size() == 0;
    }

    Stream<Entry<K, V>> sorted(Comparator<Entry<K, V>> comparator);

    @FunctionalInterface
    interface HashFunction<K> {
        int hash(K k, int mod);
    }

    @FunctionalInterface
    interface EqualsFunction<K> {
        boolean equals(K k1, K k2);
    }

    /**
     * Как можно оптимизировать работу с Entry?
     *
     * @param <K> - ключ
     * @param <V> - значение
     */
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

    abstract class AbstractHashTable<K, V> implements HashTable<K, V> {
        private final HashFunction<K> hashFunction;
        private final EqualsFunction<K> equalsFunction;

        protected AbstractHashTable(HashFunction<K> hashFunction, EqualsFunction<K> equalsFunction) {
            this.hashFunction = hashFunction;
            this.equalsFunction = equalsFunction;
        }

        public AbstractHashTable() {
            this((k, mod) -> Math.abs(Objects.hash(k) % mod), Objects::equals);
        }

        protected int keyIndex(K key, int mod) {
            return hashFunction.hash(key, mod);
        }

        protected boolean keyEquals(K k1, K k2) {
            return equalsFunction.equals(k1, k2);
        }
    }

    /**
     * Почему реализация не оптимальна?
     * Как минимум две причины.
     *
     * @param <K> - ключ
     * @param <V> - значение
     */
    final class Default<K, V> extends AbstractHashTable<K, V> {
        private final Entry<K, V>[] entries;
        private int size;

        public Default(int capacity) {
            this.entries = new Entry[capacity];
            this.size = 0;
        }

        public Default() {
            this(Character.MAX_VALUE);
        }

        @SuppressWarnings("PMD.EmptyControlStatement")
        private void resize() {
            if (size > entries.length * 8) {
                //TODO: реализовать перехеширование
            }
        }

        @Override
        public void put(K key, V value, Counter counter) {
            resize();
            int index = keyIndex(key, entries.length);
            counter.increment();
            if (entries[index] == null) {
                entries[index] = new Entry<>(key, value);
            } else {
                Entry<K, V> entry = entries[index];
                for (; entry != null; entry = entry.next) {
                    counter.increment();
                    if (keyEquals(key, entry.key)) {
                        //TODO: Уже есть. Надо заменить?
                        break;
                    } else if (entry.next == null) {
                        entry.next = new Entry<>(key, value);
                        break;
                    }
                }
            }
            ++size;
        }

        @Override
        public Optional<V> get(K key, Counter counter) {
            int index = keyIndex(key, entries.length);
            Entry<K, V> entry = entries[index];
            if (entry == null) {
                counter.increment();
                return Optional.empty();
            }
            for (; entry != null; entry = entry.next) {
                counter.increment();
                if (keyEquals(key, entry.key)) {
                    return Optional.of(entry.value);
                }
            }
            return Optional.empty();
        }

        @SuppressWarnings("PMD.CompareObjectsWithEquals")
        @Override
        public Optional<V> remove(K key, Counter counter) {
            int index = keyIndex(key, entries.length);
            Entry<K, V> entry = entries[index];
            if (entry == null) {
                counter.increment();
                return Optional.empty();
            }
            Entry<K, V> prev = entry;
            Entry<K, V> removed = entry;
            for (; entry != null; entry = entry.next) {
                counter.increment();
                if (keyEquals(key, entry.key)) {
                    removed = entry;
                    if (entry.next == null) {
                        if (prev == entry) {
                            entries[index] = null;
                            break;
                        } else {
                            prev.next = null;
                        }
                    } else {
                        if (prev == entry) {
                            entries[index] = entry.next;
                        } else {
                            prev.next = entry.next;
                        }
                    }
                    size--;
                    break;
                }
                prev = entry;
            }
            return Optional.of(removed).map(p -> p.value);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            Arrays.fill(entries, null);
            size = 0;
        }

        @SuppressWarnings("PMD.ForLoopCanBeForeach")
        @Override
        public void print() {
            for (int i = 0; i < entries.length; i++) {
                Entry<K, V> entry = entries[i];
                if (entry != null) {
                    for (; entry != null; entry = entry.next) {
                        System.out.printf("Prn. %15s: %s%n", entry.key, entry.value);
                    }
                }
            }
        }

        //FIXME: В высшей степени плохая реализация. Но зато быстрая.
        @SuppressWarnings("PMD.ForLoopCanBeForeach")
        @Override
        public Stream<Entry<K, V>> sorted(Comparator<Entry<K, V>> comparator) {
            List<Entry<K, V>> entryList = new ArrayList<>(entries.length);
            for (int i = 0; i < entries.length; i++) {
                Entry<K, V> entry = entries[i];
                if (entry != null) {
                    for (; entry != null; entry = entry.next) {
                        entryList.add(entry);
                    }
                }
            }
            return entryList.stream().sorted(comparator);
        }

        @SuppressWarnings("PMD.ForLoopCanBeForeach")
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < entries.length; ++i) {
                Entry<K, V> entry = entries[i];
                if (entry != null) {
                    if (!sb.isEmpty()) {
                        sb.append("\n");
                    }
                    sb.append(entry);
                }
            }
            return sb.toString();
        }
    }
}
