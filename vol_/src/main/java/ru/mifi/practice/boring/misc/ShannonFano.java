package ru.mifi.practice.boring.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Код Шеннона — Фано: сжатие без потерь, где частым символам достаются короткие коды. Символы
 * упорядочиваются по убыванию частоты и делятся на две группы так, чтобы суммарные частоты были
 * как можно ближе; первой группе приписывается бит 0, второй 1, и каждая делится дальше.
 *
 * <p>Код получается префиксным: ни один код не является началом другого, потому что символы
 * оказываются в листьях дерева деления. Значит поток битов разбирается однозначно, без
 * разделителей.
 *
 * <p>Это первый алгоритм такого рода, 1948 год, и он почти оптимален — но именно почти. Хаффман
 * годом позже показал, что деление сверху вниз иногда промахивается, а сборка снизу вверх даёт
 * гарантированно кратчайший код. Разница невелика, но она есть, и потому сегодня везде Хаффман.
 */
public final class ShannonFano {

    /** Код каждого символа текста: символ — строка из нулей и единиц. */
    public Map<Character, String> codes(String text) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Кодировать пустой текст нечем");
        }
        List<Map.Entry<Character, Integer>> counts = counted(text);
        Map<Character, String> codes = new LinkedHashMap<>();
        counts.forEach(entry -> codes.put(entry.getKey(), ""));
        split(counts, codes);
        return codes;
    }

    /** Длина закодированного текста в битах. */
    public int length(String text) {
        Map<Character, String> codes = codes(text);
        int length = 0;
        for (char symbol : text.toCharArray()) {
            length += codes.get(symbol).length();
        }
        return length;
    }

    /** Деление группы надвое по равенству частот; каждой половине дописывается свой бит. */
    private void split(List<Map.Entry<Character, Integer>> counts, Map<Character, String> codes) {
        if (counts.size() < 2) {
            return;
        }
        int border = border(counts);
        for (int at = 0; at < counts.size(); at++) {
            char symbol = counts.get(at).getKey();
            codes.put(symbol, codes.get(symbol) + (at < border ? "0" : "1"));
        }
        split(counts.subList(0, border), codes);
        split(counts.subList(border, counts.size()), codes);
    }

    /** Место деления: там, где суммы частот двух половин ближе всего друг к другу. */
    private int border(List<Map.Entry<Character, Integer>> counts) {
        int total = counts.stream().mapToInt(Map.Entry::getValue).sum();
        int border = 1;
        int head = counts.get(0).getValue();
        int best = Math.abs(total - 2 * head);
        for (int at = 1; at < counts.size() - 1; at++) {
            head += counts.get(at).getValue();
            int difference = Math.abs(total - 2 * head);
            if (difference < best) {
                best = difference;
                border = at + 1;
            }
        }
        return border;
    }

    private List<Map.Entry<Character, Integer>> counted(String text) {
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char symbol : text.toCharArray()) {
            counts.merge(symbol, 1, Integer::sum);
        }
        List<Map.Entry<Character, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort(Comparator.comparing(Map.Entry<Character, Integer>::getValue).reversed()
            .thenComparing(Map.Entry::getKey));
        return sorted;
    }
}
