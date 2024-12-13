package ru.mifi.practice.vol7.distance;

import ru.mifi.practice.vol7.Counter;

import java.util.Arrays;

public interface Levenshtein extends Distance {

    abstract class AbstractLevenshtein implements Levenshtein {
        protected static int cost(char a, char b) {
            return a == b ? 0 : 1;
        }

        protected static int min(int... numbers) {
            return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
        }
    }

    final class LevenshteinRecursion extends AbstractLevenshtein {

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
                + cost(s1.charAt(0), s2.charAt(0));
            int insertion = distance(s1, s2.substring(1), counter) + 1;
            int deletion = distance(s1.substring(1), s2, counter) + 1;

            return min(substitution, insertion, deletion);
        }
    }

    final class VagnerFisherDynamicus extends AbstractLevenshtein {
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
                        table[i][j] = min(table[i - 1][j - 1] + cost(s1.charAt(i - 1), s2.charAt(j - 1)),
                            table[i - 1][j] + 1,
                            table[i][j - 1] + 1);
                    }
                }
            }

            return table[s1.length()][s2.length()];
        }
    }
}
