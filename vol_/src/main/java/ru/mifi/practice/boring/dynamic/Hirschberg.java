package ru.mifi.practice.boring.dynamic;

/**
 * Наибольшая общая подпоследовательность по Хиршбергу — то же динамическое программирование, но
 * в линейной памяти. Ключевое наблюдение: чтобы посчитать длины, вся матрица не нужна — хватает
 * предыдущей строки. Беда в том, что без матрицы нечем идти обратным ходом, а ответ нужен целиком.
 *
 * <p>Хиршберг обходит это делением. Первая строка разрезается пополам. Для верхней половины
 * считаются длины ответов со всеми префиксами второй строки, для нижней — то же самое с конца, на
 * развёрнутых строках. Столбец, где сумма этих двух длин наибольшая, и есть место, в котором
 * оптимальный ответ пересекает середину. Значит вторая строка тоже делится — ровно в этом
 * столбце, — и обе половины решаются рекурсивно.
 *
 * <p>Работы получается вдвое больше, чем у табличного способа, но памяти — O(min(n, m)) вместо
 * O(n·m). Рекурсия неглубокая: на каждом уровне задача делится пополам.
 */
public final class Hirschberg implements Subsequence {

    @Override
    public String longest(String left, String right) {
        if (left.isEmpty() || right.isEmpty()) {
            return "";
        }
        if (left.length() == 1) {
            return right.indexOf(left.charAt(0)) >= 0 ? left : "";
        }
        int middle = left.length() / 2;
        int[] head = lengths(left.substring(0, middle), right);
        int[] tail = lengths(reversed(left.substring(middle)), reversed(right));
        int split = split(head, tail, right.length());
        return longest(left.substring(0, middle), right.substring(0, split))
            + longest(left.substring(middle), right.substring(split));
    }

    /** Длины ответов для всей первой строки и каждого префикса второй — одна строка матрицы. */
    private int[] lengths(String left, String right) {
        int[] previous = new int[right.length() + 1];
        int[] current = new int[right.length() + 1];
        for (int row = 1; row <= left.length(); row++) {
            for (int column = 1; column <= right.length(); column++) {
                current[column] = left.charAt(row - 1) == right.charAt(column - 1)
                    ? previous[column - 1] + 1
                    : Math.max(previous[column], current[column - 1]);
            }
            System.arraycopy(current, 0, previous, 0, current.length);
        }
        return previous;
    }

    /** Столбец, в котором ответ пересекает середину: там сумма длин с обеих сторон наибольшая. */
    private int split(int[] head, int[] tail, int length) {
        int split = 0;
        int best = -1;
        for (int column = 0; column <= length; column++) {
            int total = head[column] + tail[length - column];
            if (total > best) {
                best = total;
                split = column;
            }
        }
        return split;
    }

    private String reversed(String text) {
        return new StringBuilder(text).reverse().toString();
    }
}
