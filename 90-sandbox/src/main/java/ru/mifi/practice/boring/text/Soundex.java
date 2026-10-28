package ru.mifi.practice.boring.text;

import java.util.Locale;

/**
 * Soundex — фонетический код 1918 года, придуманный для переписи населения США. Первая буква
 * слова остаётся как есть, остальные согласные заменяются цифрами по таблице: близкие по
 * артикуляции звуки получают одну цифру, потому что на слух их путают чаще всего.
 *
 * <p>Гласные выбрасываются, но не бесследно: они разделяют одинаковые цифры. Поэтому в «Tymczak»
 * две одинаковые цифры подряд схлопываются, а разделённые гласной — нет. Код всегда одной длины:
 * буква и три цифры, недостающие добиваются нулями, лишние отбрасываются.
 */
public final class Soundex implements Phonetic {
    private static final String CODES = "01230120022455012623010202";

    @Override
    public String code(String word) {
        String letters = word.toUpperCase(Locale.ROOT).replaceAll("[^A-Z]", "");
        if (letters.isEmpty()) {
            throw new IllegalArgumentException("Слово без латинских букв фонетического кода не имеет");
        }
        StringBuilder code = new StringBuilder().append(letters.charAt(0));
        char previous = digit(letters.charAt(0));
        for (int i = 1; i < letters.length() && code.length() < 4; i++) {
            char letter = letters.charAt(i);
            char digit = digit(letter);
            if (digit != '0' && digit != previous) {
                code.append(digit);
            }
            if ("HW".indexOf(letter) < 0) {
                previous = digit;
            }
        }
        return code.append("000").substring(0, 4);
    }

    private char digit(char letter) {
        return CODES.charAt(letter - 'A');
    }
}
