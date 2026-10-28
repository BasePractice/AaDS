package ru.mifi.practice.boring.text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Суффиксный массив и массив наибольших общих префиксов к нему. Суффиксный массив — это все
 * суффиксы строки, упорядоченные лексикографически и записанные своими начальными позициями;
 * по нему подстрока ищется двоичным поиском, а повторы видны как соседние суффиксы.
 *
 * <p>Сам массив здесь строится сортировкой суффиксов — очевидно и медленно. Интересное в этом
 * классе другое: алгоритм Касаи, который считает наибольшие общие префиксы соседних суффиксов
 * за линейное время.
 *
 * <p>Хитрость Касаи в порядке обхода: суффиксы перебираются не по массиву, а по тексту слева
 * направо. Если у суффикса с позиции i общий префикс с соседом длиной k, то у суффикса с позиции
 * i+1 он не короче k−1: обе строки просто потеряли первый символ. Поэтому счётчик совпадений
 * не сбрасывается в ноль, а уменьшается на единицу, и суммарно он убывает не более n раз —
 * отсюда линейность.
 */
public final class Suffixes {
    private final String text;
    private final int[] order;
    private final int[] places;

    public Suffixes(String text) {
        this.text = text;
        this.order = sorted(text);
        this.places = places(order);
    }

    /** Начальные позиции суффиксов в лексикографическом порядке. */
    public int[] order() {
        return order.clone();
    }

    /**
     * Длины наибольших общих префиксов соседних суффиксов: ячейка i — общий префикс суффиксов,
     * стоящих в массиве на местах i и i+1. Последняя ячейка нулевая — за последним соседа нет.
     */
    public int[] prefixes() {
        int[] prefixes = new int[text.length()];
        int matched = 0;
        for (int at = 0; at < text.length(); at++) {
            if (places[at] == text.length() - 1) {
                matched = 0;
                continue;
            }
            int neighbour = order[places[at] + 1];
            while (at + matched < text.length() && neighbour + matched < text.length()
                && text.charAt(at + matched) == text.charAt(neighbour + matched)) {
                ++matched;
            }
            prefixes[places[at]] = matched;
            matched = Math.max(0, matched - 1);
        }
        return prefixes;
    }

    private int[] sorted(String text) {
        List<Integer> suffixes = new ArrayList<>();
        for (int at = 0; at < text.length(); at++) {
            suffixes.add(at);
        }
        suffixes.sort(Comparator.comparing(text::substring));
        int[] order = new int[suffixes.size()];
        for (int place = 0; place < suffixes.size(); place++) {
            order[place] = suffixes.get(place);
        }
        return order;
    }

    /** Обратная перестановка: по позиции суффикса в тексте — его место в массиве. */
    private int[] places(int[] order) {
        int[] places = new int[order.length];
        for (int place = 0; place < order.length; place++) {
            places[order[place]] = place;
        }
        return places;
    }
}
