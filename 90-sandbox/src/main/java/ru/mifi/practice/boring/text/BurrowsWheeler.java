package ru.mifi.practice.boring.text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Преобразование Барроуза — Уилера: перестановка символов текста, после которой одинаковые буквы
 * сбиваются в длинные группы. Само по себе оно ничего не сжимает — длина не меняется, — но после
 * него отлично работает кодирование длин серий и арифметическое кодирование. На этом стоит bzip2.
 *
 * <p>Идея: выписать все циклические сдвиги текста, отсортировать их и взять последний столбец. В
 * отсортированном списке рядом стоят сдвиги с одинаковым началом, а последний символ сдвига —
 * это символ, стоящий в тексте перед этим началом. В естественном языке перед одинаковыми
 * началами стоят одни и те же буквы — отсюда и группы.
 *
 * <p>Преобразование обратимо, и это самое удивительное в нём: из одного последнего столбца
 * восстанавливается весь текст. Первый столбец — это тот же столбец, только отсортированный,
 * а связь между ними даёт порядок обхода. Чтобы восстановление было однозначным, к тексту
 * приписывается маркер конца — символ, которого в тексте больше нигде нет.
 */
public final class BurrowsWheeler {
    private static final char MARKER = '\u0003';

    public String transform(String text) {
        if (text.indexOf(MARKER) >= 0) {
            throw new IllegalArgumentException("Текст содержит служебный маркер конца строки");
        }
        String marked = text + MARKER;
        List<Integer> shifts = new ArrayList<>();
        for (int shift = 0; shift < marked.length(); shift++) {
            shifts.add(shift);
        }
        shifts.sort(Comparator.comparing(shift -> rotated(marked, shift)));
        StringBuilder last = new StringBuilder();
        for (int shift : shifts) {
            last.append(marked.charAt((shift + marked.length() - 1) % marked.length()));
        }
        return last.toString();
    }

    /**
     * Восстановление по последнему столбцу. Строится перестановка: для i-й строки указывается,
     * какая строка идёт за ней при обходе текста, — и по ней собирается исходная строка.
     */
    public String restore(String transformed) {
        int marker = transformed.indexOf(MARKER);
        if (marker < 0) {
            throw new IllegalArgumentException("В преобразовании нет маркера конца строки");
        }
        int[] next = next(transformed);
        StringBuilder text = new StringBuilder();
        int current = next[marker];
        while (current != marker) {
            text.append(transformed.charAt(current));
            current = next[current];
        }
        return text.toString();
    }

    /**
     * Для каждой строки отсортированной таблицы — номер строки, начинающейся со следующего
     * символа текста. Одинаковые символы в обоих столбцах идут в одном порядке, поэтому хватает
     * подсчёта: k-е вхождение буквы в последнем столбце отвечает k-му вхождению в первом.
     */
    private int[] next(String transformed) {
        int[] counts = new int[Character.MAX_VALUE + 1];
        for (char symbol : transformed.toCharArray()) {
            counts[symbol]++;
        }
        int[] starts = new int[counts.length];
        int start = 0;
        for (int symbol = 0; symbol < counts.length; symbol++) {
            starts[symbol] = start;
            start += counts[symbol];
        }
        int[] next = new int[transformed.length()];
        for (int row = 0; row < transformed.length(); row++) {
            char symbol = transformed.charAt(row);
            next[starts[symbol]] = row;
            starts[symbol]++;
        }
        return next;
    }

    private String rotated(String text, int shift) {
        return text.substring(shift) + text.substring(0, shift);
    }
}
