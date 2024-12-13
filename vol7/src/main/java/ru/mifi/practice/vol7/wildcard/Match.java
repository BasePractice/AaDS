package ru.mifi.practice.vol7.wildcard;

import ru.mifi.practice.vol7.Counter;

public interface Match {
    boolean isMatch(String pattern, String text, Counter counter);

    final class DefaultMatch implements Match {

        @Override
        public boolean isMatch(String pattern, String text, Counter counter) {
            int m = text.length();
            int n = pattern.length();
            boolean[][] table = new boolean[m + 1][n + 1];
            table[0][0] = true;

            for (int j = 1; j <= n; j++) {
                if (pattern.charAt(j - 1) == '*') {
                    table[0][j] = table[0][j - 1];
                }
            }

            for (int i = 1; i <= m; i++) {
                counter.increment();
                for (int j = 1; j <= n; j++) {
                    counter.increment();
                    char textChar = text.charAt(i - 1);
                    char patternChar = pattern.charAt(j - 1);

                    if (patternChar == textChar || patternChar == '?') {
                        table[i][j] = table[i - 1][j - 1];
                    } else if (patternChar == '*') {
                        table[i][j] = table[i - 1][j] || table[i][j - 1];
                    } else {
                        table[i][j] = false;
                    }
                }
            }
            return table[m][n];
        }
    }

    final class AbbreviationMatch implements Match {

        @Override
        public boolean isMatch(String text, String abb, Counter counter) {
            int n = text.length();
            int m = abb.length();

            boolean[][] table = new boolean[n + 1][m + 1];

            table[0][0] = true;

            for (int i = 0; i < n; i++) {
                counter.increment();
                for (int j = 0; j <= m; j++) {
                    counter.increment();
                    if (table[i][j]) {
                        if (j < m && Character.toUpperCase(text.charAt(i)) == abb.charAt(j)) {
                            table[i + 1][j + 1] = true;
                        }
                        if (Character.isLowerCase(text.charAt(i)) || Character.isWhitespace(text.charAt(i))) {
                            table[i + 1][j] = true;
                        }
                    }
                }
            }
            return table[n][m];
        }
    }
}
