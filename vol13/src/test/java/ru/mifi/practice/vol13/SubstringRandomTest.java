package ru.mifi.practice.vol13;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка обоих алгоритмов поиска подстроки со штатным indexOf на случайных входах.
 *
 * <p>Алфавит намеренно узкий — три буквы, — чтобы совпадения и ложные срабатывания хеша
 * попадались часто, а не раз в тысячу прогонов.
 */
@DisplayName("Поиск подстроки на случайных входах")
final class SubstringRandomTest {

    @DisplayName("Кнут — Моррис — Пратт находит то же первое вхождение, что и indexOf")
    @Test
    @Timeout(10)
    void agreesWithIndexOfOnTheFirstOccurrence() {
        assertThat("Knuth Morris Pratt disagrees with indexOf on the first occurrence",
            disagreements(new Substring.Knuth()), is(List.of()));
    }

    @DisplayName("Рабин — Карп находит то же первое вхождение, что и indexOf")
    @Test
    @Timeout(10)
    void agreesWithIndexOfOnTheFirstOccurrenceWhenHashed() {
        assertThat("Rabin Karp disagrees with indexOf on the first occurrence",
            disagreements(new Substring.RabinKarp()), is(List.of()));
    }

    @DisplayName("Все вхождения перечисляются полностью, включая перекрывающиеся")
    @Test
    @Timeout(10)
    void listsEveryOccurrenceIncludingOverlapping() {
        assertThat("the search dont list every occurrence",
            new Substring.Knuth().all("aaaa", "aa", Counter.create()), is(List.of(0, 1, 2)));
    }

    @DisplayName("Оба алгоритма перечисляют одни и те же вхождения")
    @Test
    @Timeout(10)
    void agreeWithEachOtherOnEveryOccurrence() {
        Random random = new Random(20260804L);
        List<String> mismatches = new ArrayList<>();
        for (int attempt = 0; attempt < 500; attempt++) {
            String text = word(random, random.nextInt(40));
            String pattern = word(random, 1 + random.nextInt(4));
            List<Integer> knuth = new Substring.Knuth().all(text, pattern, Counter.create());
            List<Integer> hashed = new Substring.RabinKarp().all(text, pattern, Counter.create());
            if (!knuth.equals(hashed)) {
                mismatches.add(pattern + " в " + text + ": " + knuth + " против " + hashed);
            }
        }
        assertThat("the two algorithms disagree on the occurrences", mismatches, is(List.of()));
    }

    private static List<String> disagreements(Substring substring) {
        Random random = new Random(20260804L);
        List<String> mismatches = new ArrayList<>();
        for (int attempt = 0; attempt < 500; attempt++) {
            String text = word(random, random.nextInt(40));
            String pattern = word(random, 1 + random.nextInt(4));
            OptionalInt found = substring.first(text, pattern, Counter.create());
            int expected = text.indexOf(pattern);
            if (found.orElse(-1) != expected) {
                mismatches.add(pattern + " в " + text + ": " + found.orElse(-1) + " вместо " + expected);
            }
        }
        return mismatches;
    }

    private static String word(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + random.nextInt(3)));
        }
        return text.toString();
    }
}
