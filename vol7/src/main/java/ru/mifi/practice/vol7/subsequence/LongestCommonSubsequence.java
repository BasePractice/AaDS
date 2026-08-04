package ru.mifi.practice.vol7.subsequence;

import ru.mifi.practice.commons.Counter;

/** Длина наибольшей общей подпоследовательности двух строк. */
public interface LongestCommonSubsequence {
    int longestCommonSubsequence(String text1, String text2, Counter counter);

    final class Default implements LongestCommonSubsequence {

        @Override
        public int longestCommonSubsequence(String text1, String text2, Counter counter) {
            int rows = text1.length();
            int columns = text2.length();
            int[][] table = new int[rows + 1][columns + 1];
            for (int i = 1; i <= rows; i++) {
                counter.increment();
                for (int j = 1; j <= columns; j++) {
                    counter.increment();
                    if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    } else {
                        table[i][j] = Math.max(table[i][j - 1], table[i - 1][j]);
                    }
                }
            }
            return table[rows][columns];
        }
    }
}
