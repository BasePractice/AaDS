package ru.mifi.practice.boring.text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Поиск подстроки хешем — алгоритм Рабина — Карпа. Сравнивать окно текста с образцом посимвольно
 * дорого; вместо этого сравниваются числа: хеш образца и хеш окна той же длины. Хеш полиномиальный,
 * поэтому при сдвиге окна на символ он пересчитывается за одно действие — вычесть вклад ушедшего
 * символа, умножить на основание, прибавить вошедший.
 *
 * <p>Совпадение хешей ничего не доказывает: разные строки могут дать одно число. Поэтому
 * найденное место всё равно проверяется посимвольно — в среднем это случается редко, и весь
 * поиск стоит O(n + m), а в патологическом случае вырождается в O(n·m).
 *
 * <p>Модуль большой и простой, а арифметика ведётся в long: произведение двух чисел около
 * миллиарда в int не помещается.
 */
public final class RabinKarp implements Search {
    private static final long BASE = 31;
    private static final long MOD = 1_000_000_007L;

    @Override
    public Map<String, List<Integer>> positions(String text, List<String> patterns) {
        Map<String, List<Integer>> found = new LinkedHashMap<>();
        for (String pattern : patterns) {
            found.put(pattern, positions(text, pattern));
        }
        return found;
    }

    private List<Integer> positions(String text, String pattern) {
        List<Integer> found = new ArrayList<>();
        if (pattern.isEmpty() || pattern.length() > text.length()) {
            return found;
        }
        long highest = highest(pattern.length());
        long wanted = hash(pattern, 0, pattern.length());
        long window = hash(text, 0, pattern.length());
        for (int at = 0; at + pattern.length() <= text.length(); at++) {
            if (wanted == window && text.startsWith(pattern, at)) {
                found.add(at);
            }
            if (at + pattern.length() < text.length()) {
                window = rolled(window, text.charAt(at), text.charAt(at + pattern.length()), highest);
            }
        }
        return found;
    }

    /** Сдвиг окна на символ: вклад ушедшего символа снимается, вошедший добавляется. */
    private long rolled(long window, char gone, char came, long highest) {
        long rest = (window - gone * highest % MOD + MOD * MOD) % MOD;
        return (rest * BASE + came) % MOD;
    }

    private long hash(String text, int from, int length) {
        long hash = 0;
        for (int i = from; i < from + length; i++) {
            hash = (hash * BASE + text.charAt(i)) % MOD;
        }
        return hash;
    }

    private long highest(int length) {
        long highest = 1;
        for (int i = 1; i < length; i++) {
            highest = highest * BASE % MOD;
        }
        return highest;
    }
}
