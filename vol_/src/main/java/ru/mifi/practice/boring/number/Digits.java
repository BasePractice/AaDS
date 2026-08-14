package ru.mifi.practice.boring.number;

import java.util.Arrays;

/**
 * Длинное число как массив десятичных цифр — общая структура для умножения. Цифры лежат от
 * младшей к старшей: так удобнее считать, потому что перенос всегда идёт вперёд по массиву, а
 * дописывание старших разрядов не требует сдвигать всё остальное.
 *
 * <p>Основание взято десятичным нарочно: в него можно ткнуть пальцем и проверить руками, а
 * настоящие библиотеки берут 2³² и выигрывают в скорости, ничего не меняя по сути.
 *
 * <p>Число неизменяемо и всегда нормализовано — ведущих нулей нет, ноль записан пустым массивом.
 */
public final class Digits {
    private final int[] digits;

    public Digits(String decimal) {
        this(parsed(decimal));
    }

    Digits(int[] digits) {
        this.digits = trimmed(digits);
    }

    public int size() {
        return digits.length;
    }

    public int digit(int at) {
        return at < digits.length ? digits[at] : 0;
    }

    /** Младшие цифры числа — из них состоит правая половина при разбиении. */
    Digits low(int length) {
        return new Digits(Arrays.copyOfRange(digits, 0, Math.min(length, digits.length)));
    }

    /** Старшие цифры числа — левая половина при разбиении. */
    Digits high(int length) {
        return length >= digits.length
            ? new Digits(new int[0])
            : new Digits(Arrays.copyOfRange(digits, length, digits.length));
    }

    Digits shifted(int places) {
        if (digits.length == 0) {
            return this;
        }
        int[] shifted = new int[digits.length + places];
        System.arraycopy(digits, 0, shifted, places, digits.length);
        return new Digits(shifted);
    }

    Digits plus(Digits other) {
        int[] sum = new int[Math.max(size(), other.size()) + 1];
        int carry = 0;
        for (int at = 0; at < sum.length; at++) {
            int total = digit(at) + other.digit(at) + carry;
            sum[at] = total % 10;
            carry = total / 10;
        }
        return new Digits(sum);
    }

    /** Разность; вычитаемое не должно превышать уменьшаемое — иначе числа перестали бы быть целыми. */
    Digits minus(Digits other) {
        int[] rest = new int[size()];
        int borrow = 0;
        for (int at = 0; at < rest.length; at++) {
            int total = digit(at) - other.digit(at) - borrow;
            borrow = total < 0 ? 1 : 0;
            rest[at] = total + borrow * 10;
        }
        if (borrow != 0) {
            throw new IllegalArgumentException("Вычитаемое больше уменьшаемого, отрицательных чисел здесь нет");
        }
        return new Digits(rest);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Digits digits && Arrays.equals(this.digits, digits.digits);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(digits);
    }

    @Override
    public String toString() {
        if (digits.length == 0) {
            return "0";
        }
        StringBuilder decimal = new StringBuilder();
        for (int at = digits.length - 1; at >= 0; at--) {
            decimal.append((char) ('0' + digits[at]));
        }
        return decimal.toString();
    }

    private static int[] parsed(String decimal) {
        int[] digits = new int[decimal.length()];
        for (int at = 0; at < decimal.length(); at++) {
            digits[at] = decimal.charAt(decimal.length() - 1 - at) - '0';
        }
        return digits;
    }

    private static int[] trimmed(int[] digits) {
        int length = digits.length;
        while (length > 0 && digits[length - 1] == 0) {
            --length;
        }
        return Arrays.copyOf(digits, length);
    }
}
