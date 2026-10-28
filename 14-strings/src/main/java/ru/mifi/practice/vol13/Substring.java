package ru.mifi.practice.vol13;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * Поиск подстроки из раздела 15 курса.
 */
public interface Substring {

    /**
     * Позиция первого вхождения образца в текст.
     */
    OptionalInt first(String text, String pattern, Counter counter);

    /**
     * Позиции всех вхождений, включая перекрывающиеся.
     */
    List<Integer> all(String text, String pattern, Counter counter);

    /**
     * Алгоритм Кнута — Морриса — Пратта. Префикс-функция для каждой позиции образца хранит длину
     * наибольшего собственного префикса, который одновременно является суффиксом. При несовпадении
     * она позволяет сдвинуться сразу на нужную величину, не возвращаясь по тексту, поэтому весь
     * поиск стоит O(n + m) при O(m) дополнительной памяти.
     */
    final class Knuth implements Substring {

        /**
         * Префикс-функция образца. Вынесена наружу, потому что сама по себе отвечает на вопросы
         * о периодах строки, а не только обслуживает поиск.
         */
        public static int[] prefixes(String pattern, Counter counter) {
            int[] prefixes = new int[pattern.length()];
            int length = 0;
            for (int i = 1; i < pattern.length(); i++) {
                while (length > 0 && pattern.charAt(i) != pattern.charAt(length)) {
                    counter.increment();
                    length = prefixes[length - 1];
                }
                counter.increment();
                if (pattern.charAt(i) == pattern.charAt(length)) {
                    ++length;
                }
                prefixes[i] = length;
            }
            return prefixes;
        }

        @Override
        public OptionalInt first(String text, String pattern, Counter counter) {
            List<Integer> found = search(text, pattern, counter, true);
            if (found.isEmpty()) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(found.get(0));
        }

        @Override
        public List<Integer> all(String text, String pattern, Counter counter) {
            return search(text, pattern, counter, false);
        }

        private List<Integer> search(String text, String pattern, Counter counter, boolean stopAtFirst) {
            List<Integer> found = new ArrayList<>();
            if (pattern.isEmpty() || pattern.length() > text.length()) {
                return found;
            }
            int[] prefixes = prefixes(pattern, counter);
            int matched = 0;
            for (int i = 0; i < text.length(); i++) {
                while (matched > 0 && text.charAt(i) != pattern.charAt(matched)) {
                    counter.increment();
                    matched = prefixes[matched - 1];
                }
                counter.increment();
                if (text.charAt(i) == pattern.charAt(matched)) {
                    ++matched;
                }
                if (matched == pattern.length()) {
                    found.add(i - pattern.length() + 1);
                    if (stopAtFirst) {
                        return found;
                    }
                    matched = prefixes[matched - 1];
                }
            }
            return found;
        }
    }

    /**
     * Алгоритм Рабина — Карпа. Сравниваются хеши образца и окна той же длины; при сдвиге окна на
     * символ хеш пересчитывается за O(1) вычитанием вклада уходящего символа и добавлением
     * входящего. Совпадение хешей проверяется посимвольно, потому что коллизии неизбежны.
     * В среднем O(n + m). Арифметика ведётся в long: при модуле около двух миллиардов
     * произведение не помещается в int.
     */
    final class RabinKarp implements Substring {
        private static final long BASE = 31;
        private static final long MOD = 1_000_000_007L;

        @Override
        public OptionalInt first(String text, String pattern, Counter counter) {
            List<Integer> found = search(text, pattern, counter, true);
            if (found.isEmpty()) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(found.get(0));
        }

        @Override
        public List<Integer> all(String text, String pattern, Counter counter) {
            return search(text, pattern, counter, false);
        }

        private List<Integer> search(String text, String pattern, Counter counter, boolean stopAtFirst) {
            List<Integer> found = new ArrayList<>();
            int length = pattern.length();
            if (length == 0 || length > text.length()) {
                return found;
            }
            long highest = 1;
            for (int i = 1; i < length; i++) {
                highest = highest * BASE % MOD;
            }
            long patternHash = 0;
            long windowHash = 0;
            for (int i = 0; i < length; i++) {
                patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
                windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
            }
            for (int i = 0; i + length <= text.length(); i++) {
                counter.increment();
                if (patternHash == windowHash && text.startsWith(pattern, i)) {
                    found.add(i);
                    if (stopAtFirst) {
                        return found;
                    }
                }
                if (i + length < text.length()) {
                    windowHash = (windowHash - text.charAt(i) * highest % MOD + MOD * MOD) % MOD;
                    windowHash = (windowHash * BASE + text.charAt(i + length)) % MOD;
                }
            }
            return found;
        }
    }
}
