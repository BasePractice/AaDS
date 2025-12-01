package ru.mifi.practice.vol7.subsequence;

import ru.mifi.practice.vol7.Counter;

//Наибольшая общая подпоследовательность
public interface LongestCommonSubsequence {
    int longestCommonSubsequence(String text1, String text2, Counter counter);

    final class Default implements LongestCommonSubsequence {

        @Override
        public int longestCommonSubsequence(String text1, String text2, Counter counter) {
            int n = text1.length();
            int m = text2.length();
            int[][] table = new int[n + 1][m + 1];
            for (int i = 1; i <= n; i++) {
                counter.increment();
                for (int j = 1; j <= m; j++) {
                    counter.increment();
                    if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    } else {
                        table[i][j] = Math.max(table[i][j - 1], table[i - 1][j]);
                    }
                }
            }
            return table[n][m];
        }
    }
}
