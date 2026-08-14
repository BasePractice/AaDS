package ru.mifi.practice.vol4.map;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Хеш-таблица с открытой адресацией: записи лежат прямо в массиве, а на занятое место
 * претендент садится в следующую свободную ячейку — линейное пробирование. Цепочек нет вовсе,
 * поэтому нет и узлов со ссылками: таблица помещается в один массив и дружит с кешем процессора.
 *
 * <p>Плата за это — удаление. Просто обнулить ячейку нельзя: разрыв в цепочке проб отрежет
 * записи, которые сели за ней. Поэтому на месте удалённой записи остаётся надгробие — метка
 * «здесь было занято»: поиск идёт сквозь неё дальше, а вставка занимает её первой.
 *
 * <p>Перехеширование честное: как только занято больше половины массива, заводится вдвое больший
 * и все живые записи раскладываются в нём заново — от нового размера зависит и номер ячейки.
 * Без этого пробы растянулись бы на всю таблицу и поиск выродился бы в перебор.
 */
public final class AddressTable<K, V> extends HashTable.AbstractHashTable<K, V> {
    private HashTable.Entry<K, V>[] entries;
    private boolean[] tombstones;
    private int size;
    private int used;

    public AddressTable(int capacity) {
        this(capacity, (key, mod) -> Math.abs(Objects.hash(key) % mod), Objects::equals);
    }

    @SuppressWarnings("unchecked")
    public AddressTable(int capacity, HashTable.HashFunction<K> hashFunction, HashTable.EqualsFunction<K> equalsFunction) {
        super(hashFunction, equalsFunction);
        this.entries = new HashTable.Entry[Math.max(capacity, 2)];
        this.tombstones = new boolean[Math.max(capacity, 2)];
        this.size = 0;
        this.used = 0;
    }

    @Override
    public void put(K key, V value, Counter counter) {
        if (used * 2 >= entries.length) {
            rehash();
        }
        int free = -1;
        for (int probe = 0; probe < entries.length; probe++) {
            int slot = slot(key, probe);
            counter.increment();
            if (entries[slot] == null) {
                if (!tombstones[slot]) {
                    place(key, value, free == -1 ? slot : free);
                    return;
                }
                free = free == -1 ? slot : free;
            } else if (keyEquals(key, entries[slot].key)) {
                entries[slot].value = value;
                return;
            }
        }
        place(key, value, free);
    }

    @Override
    public Optional<V> get(K key, Counter counter) {
        for (int probe = 0; probe < entries.length; probe++) {
            int slot = slot(key, probe);
            counter.increment();
            if (entries[slot] == null && !tombstones[slot]) {
                return Optional.empty();
            }
            if (entries[slot] != null && keyEquals(key, entries[slot].key)) {
                return Optional.of(entries[slot].value);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<V> remove(K key, Counter counter) {
        for (int probe = 0; probe < entries.length; probe++) {
            int slot = slot(key, probe);
            counter.increment();
            if (entries[slot] == null && !tombstones[slot]) {
                return Optional.empty();
            }
            if (entries[slot] != null && keyEquals(key, entries[slot].key)) {
                final V value = entries[slot].value;
                entries[slot] = null;
                tombstones[slot] = true;
                --size;
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        entries = new HashTable.Entry[entries.length];
        tombstones = new boolean[entries.length];
        size = 0;
        used = 0;
    }

    @Override
    public void print() {
        for (HashTable.Entry<K, V> entry : living()) {
            System.out.printf("Prn. %15s: %s%n", entry.key, entry.value);
        }
    }

    @Override
    public Stream<HashTable.Entry<K, V>> sorted(Comparator<HashTable.Entry<K, V>> comparator) {
        return living().stream().sorted(comparator);
    }

    private void place(K key, V value, int slot) {
        if (!tombstones[slot]) {
            ++used;
        }
        entries[slot] = new HashTable.Entry<>(key, value);
        tombstones[slot] = false;
        ++size;
    }

    private int slot(K key, int probe) {
        return (keyIndex(key, entries.length) + probe) % entries.length;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        final List<HashTable.Entry<K, V>> living = living();
        entries = new HashTable.Entry[entries.length * 2];
        tombstones = new boolean[entries.length];
        size = 0;
        used = 0;
        living.forEach(entry -> put(entry.key, entry.value, Counter.create()));
    }

    private List<HashTable.Entry<K, V>> living() {
        List<HashTable.Entry<K, V>> living = new ArrayList<>(size);
        for (HashTable.Entry<K, V> entry : entries) {
            if (entry != null) {
                living.add(entry);
            }
        }
        return living;
    }

    @Override
    public String toString() {
        return living().toString();
    }
}
