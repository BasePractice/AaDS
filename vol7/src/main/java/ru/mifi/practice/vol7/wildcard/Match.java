package ru.mifi.practice.vol7.wildcard;

import ru.mifi.practice.commons.Counter;

/** Tests whether a wildcard pattern matches a text. */
public interface Match {
    boolean isMatch(String pattern, String text, Counter counter);

    final class DefaultMatch implements Match {

        @Override
        public boolean isMatch(String pattern, String text, Counter counter) {
            int rows = text.length();
            int columns = pattern.length();
            boolean[][] table = new boolean[rows + 1][columns + 1];
            table[0][0] = true;
            for (int j = 1; j <= columns; j++) {
                if (pattern.charAt(j - 1) == '*') {
                    table[0][j] = table[0][j - 1];
                }
            }
            for (int i = 1; i <= rows; i++) {
                counter.increment();
                for (int j = 1; j <= columns; j++) {
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
            return table[rows][columns];
        }
    }

    //FIXME Свести таблицу аббревиатуры к одномерной по столбцам, отложено чтобы не трогать зелёный алгоритм
    final class AbbreviationMatch implements Match {

        /**
         * Здесь шаблон — это аббревиатура, а второй аргумент — проверяемый текст,
         * как и в контракте интерфейса: isMatch(pattern, text, counter).
         */
        @Override
        public boolean isMatch(String abb, String text, Counter counter) {
            int rows = text.length();
            int columns = abb.length();
            boolean[][] table = new boolean[rows + 1][columns + 1];
            table[0][0] = true;
            for (int i = 0; i < rows; i++) {
                counter.increment();
                for (int j = 0; j <= columns; j++) {
                    counter.increment();
                    if (table[i][j]) {
                        if (j < columns && Character.toUpperCase(text.charAt(i)) == abb.charAt(j)) {
                            table[i + 1][j + 1] = true;
                        }
                        if (Character.isLowerCase(text.charAt(i)) || Character.isWhitespace(text.charAt(i))) {
                            table[i + 1][j] = true;
                        }
                    }
                }
            }
            return table[rows][columns];
        }
    }
}
