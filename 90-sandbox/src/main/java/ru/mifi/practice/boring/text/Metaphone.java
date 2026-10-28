package ru.mifi.practice.boring.text;

import java.util.Locale;

/**
 * Metaphone — фонетический код 1990 года, пришедший на смену Soundex. Разница в подходе: Soundex
 * заменяет буквы цифрами по таблице, не глядя на соседей, а Metaphone читает слово правилами и
 * выдаёт согласные звуки. Поэтому «ph» превращается в «f», «ck» — в «k», а «tion» звучит как
 * «шн», хотя по буквам это совсем не так.
 *
 * <p>Гласные остаются только в начале слова: внутри они на звучание фамилии почти не влияют, а
 * различают одинаково звучащие написания. Длина кода не ограничена — этим он тоже отличается от
 * Soundex с его четырьмя знаками.
 *
 * <p>Здесь взята основная часть правил — та, которой хватает, чтобы увидеть идею и разницу с
 * Soundex. Полный набор правил вместе с исключениями заметно длиннее и наглядности не добавляет.
 */
public final class Metaphone implements Phonetic {

    @Override
    public String code(String word) {
        String letters = word.toUpperCase(Locale.ROOT).replaceAll("[^A-Z]", "");
        if (letters.isEmpty()) {
            throw new IllegalArgumentException("Слово без латинских букв фонетического кода не имеет");
        }
        StringBuilder code = new StringBuilder();
        for (int at = 0; at < letters.length(); at++) {
            if (at == 0 || letters.charAt(at) != letters.charAt(at - 1)) {
                code.append(sound(letters, at));
            }
        }
        return code.toString();
    }

    private String sound(String letters, int at) {
        char letter = letters.charAt(at);
        if ("AEIOU".indexOf(letter) >= 0) {
            return at == 0 ? String.valueOf(letter) : "";
        }
        return consonant(letters, at);
    }

    private String consonant(String letters, int at) {
        char letter = letters.charAt(at);
        return switch (letter) {
            case 'B' -> at == letters.length() - 1 && at > 0 && letters.charAt(at - 1) == 'M' ? "" : "B";
            case 'C' -> next(letters, at) == 'H' ? "X" : "IEY".indexOf(next(letters, at)) >= 0 ? "S" : "K";
            case 'D' -> next(letters, at) == 'G' ? "J" : "T";
            case 'G' -> next(letters, at) == 'H' || next(letters, at) == 'N' ? "" : "K";
            case 'H' -> silent(letters, at) ? "" : "AEIOU".indexOf(next(letters, at)) >= 0 ? "H" : "";
            case 'K' -> at > 0 && letters.charAt(at - 1) == 'C' ? "" : "K";
            case 'P' -> next(letters, at) == 'H' ? "F" : "P";
            case 'Q' -> "K";
            case 'S' -> next(letters, at) == 'H' ? "X" : "S";
            case 'T' -> next(letters, at) == 'H' ? "0" : "T";
            case 'V' -> "F";
            case 'W', 'Y' -> "AEIOU".indexOf(next(letters, at)) >= 0 ? String.valueOf(letter) : "";
            case 'X' -> "KS";
            case 'Z' -> "S";
            default -> String.valueOf(letter);
        };
    }

    /** H после согласной, с которой он составляет один звук, уже учтён этой согласной. */
    private boolean silent(String letters, int at) {
        return at > 0 && "CSPTG".indexOf(letters.charAt(at - 1)) >= 0;
    }

    private char next(String letters, int at) {
        return at + 1 < letters.length() ? letters.charAt(at + 1) : ' ';
    }
}
