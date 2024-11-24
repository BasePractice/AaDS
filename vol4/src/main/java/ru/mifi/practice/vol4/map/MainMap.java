package ru.mifi.practice.vol4.map;

import ru.mifi.practice.vol4.Counter;

public abstract class MainMap {
    public static void main(String[] args) {
        HashTable<String, String> map = new HashTable.Default<>(3);
        add("Пушкин", "наше все", map);
        add("Дантес", "не наше, не все", map);
        add("Блок", "аптека, улица, фонарь", map);
        add("Есенин", "осень настала", map);
        add("Чуковский", "а лисички взяли спички", map);
        add("Народ", "из-за леса из-за гор", map);
        System.out.println(map);
    }

    private static void add(String key, String value, HashTable<String, String> map) {
        Counter.Default counter = new Counter.Default();
        map.put(key, value, counter);
        System.out.printf("Add. %9s: %s%n", key, counter);
    }
}
