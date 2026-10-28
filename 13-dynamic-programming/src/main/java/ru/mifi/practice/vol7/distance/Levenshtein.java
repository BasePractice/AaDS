package ru.mifi.practice.vol7.distance;

import ru.mifi.practice.commons.Counter;

/** Редакционное расстояние Левенштейна между двумя строками. */
public interface Levenshtein extends Distance {

    final class LevenshteinRecursion implements Levenshtein {

        @Override
        public int distance(String s1, String s2, Counter counter) {
            if (s1.isEmpty()) {
                return s2.length();
            }
            if (s2.isEmpty()) {
                return s1.length();
            }
            counter.increment();
            int substitution = distance(s1.substring(1), s2.substring(1), counter)
                + (s1.charAt(0) == s2.charAt(0) ? 0 : 1);
            int insertion = distance(s1, s2.substring(1), counter) + 1;
            int deletion = distance(s1.substring(1), s2, counter) + 1;
            return Math.min(substitution, Math.min(insertion, deletion));
        }
    }

    final class VagnerFisherDynamited implements Levenshtein {
        @Override
        public int distance(String s1, String s2, Counter counter) {
            int[][] table = new int[s1.length() + 1][s2.length() + 1];
            for (int i = 0; i <= s1.length(); i++) {
                counter.increment();
                for (int j = 0; j <= s2.length(); j++) {
                    counter.increment();
                    if (i == 0) {
                        table[i][j] = j;
                    } else if (j == 0) {
                        table[i][j] = i;
                    } else {
                        int substitution = table[i - 1][j - 1]
                            + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1);
                        table[i][j] = Math.min(substitution,
                            Math.min(table[i - 1][j] + 1, table[i][j - 1] + 1));
                    }
                }
            }
            return table[s1.length()][s2.length()];
        }
    }
}
