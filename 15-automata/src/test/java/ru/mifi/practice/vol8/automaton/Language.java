package ru.mifi.practice.vol8.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Язык автомата, перебранный по всем словам алфавита до заданной длины. Сравнивает два автомата
 * там, где сравнить их напрямую нельзя: состояния после детерминизации и минимизации другие, а
 * принимаемые слова обязаны совпадать до единого.
 */
final class Language {
    private final Automaton automaton;
    private final String alphabet;
    private final int length;

    Language(Automaton automaton, String alphabet, int length) {
        this.automaton = automaton;
        this.alphabet = alphabet;
        this.length = length;
    }

    int disagreements(Automaton other) {
        int mismatches = 0;
        for (String word : words()) {
            if (automaton.accepts(word) != other.accepts(word)) {
                ++mismatches;
            }
        }
        return mismatches;
    }

    private List<String> words() {
        List<String> words = new ArrayList<>(List.of(""));
        List<String> current = new ArrayList<>(List.of(""));
        for (int size = 0; size < length; size++) {
            List<String> next = new ArrayList<>();
            for (String word : current) {
                for (char symbol : alphabet.toCharArray()) {
                    next.add(word + symbol);
                }
            }
            words.addAll(next);
            current = next;
        }
        return words;
    }
}
