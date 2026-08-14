package ru.mifi.practice.boring.text;

/**
 * Алгоритм Манакера: находит все палиндромы строки за линейное время. Прямой перебор проверял бы
 * каждый центр вширь и стоил бы O(n²); Манакер переиспользует уже найденное.
 *
 * <p>Ключ — зеркальность: если мы стоим внутри известного палиндрома, то левая половина уже
 * посчитана, а правая — её отражение. Значит радиус в правой позиции не меньше радиуса в
 * зеркальной, пока тот не упирается в границу известного палиндрома. Дальше границы приходится
 * проверять посимвольно, но каждая такая проверка сдвигает правую границу вправо, а сдвинуться
 * она может суммарно не больше n раз.
 *
 * <p>Чётные и нечётные палиндромы — два разных случая, и чтобы не писать один и тот же код
 * дважды, строка перекладывается через разделители: «аба» превращается в «#а#б#а#». В такой
 * строке любой палиндром нечётный, а радиус в ней равен длине палиндрома в исходной.
 */
public final class Palindromes {
    private static final char SEPARATOR = '#';
    private final String text;

    public Palindromes(String text) {
        this.text = text;
    }

    /** Наибольший палиндром строки; при равенстве длин — самый левый. */
    public String longest() {
        int[] radiuses = radiuses();
        int best = 0;
        for (int at = 1; at < radiuses.length; at++) {
            if (radiuses[at] > radiuses[best]) {
                best = at;
            }
        }
        int length = radiuses[best];
        int from = (best - length) / 2;
        return text.substring(from, from + length);
    }

    /** Сколько всего палиндромов в строке, включая однобуквенные и повторяющиеся. */
    public int count() {
        int count = 0;
        for (int radius : radiuses()) {
            count += (radius + 1) / 2;
        }
        return count;
    }

    /**
     * Радиусы палиндромов с центром в каждой позиции переложенной строки. Радиус здесь удобно
     * читать так: он равен длине соответствующего палиндрома в исходной строке.
     */
    private int[] radiuses() {
        String spread = spread();
        int[] radiuses = new int[spread.length()];
        int center = 0;
        int border = 0;
        for (int at = 0; at < spread.length(); at++) {
            if (at < border) {
                radiuses[at] = Math.min(border - at, radiuses[2 * center - at]);
            }
            while (at - radiuses[at] - 1 >= 0 && at + radiuses[at] + 1 < spread.length()
                && spread.charAt(at - radiuses[at] - 1) == spread.charAt(at + radiuses[at] + 1)) {
                radiuses[at]++;
            }
            if (at + radiuses[at] > border) {
                center = at;
                border = at + radiuses[at];
            }
        }
        return radiuses;
    }

    private String spread() {
        StringBuilder spread = new StringBuilder().append(SEPARATOR);
        for (char symbol : text.toCharArray()) {
            spread.append(symbol).append(SEPARATOR);
        }
        return spread.toString();
    }
}
