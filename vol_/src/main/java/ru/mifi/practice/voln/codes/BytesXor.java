package ru.mifi.practice.voln.codes;

import java.util.Arrays;

/**
 * Утилиты для операций XOR над массивами байтов (GF(2)).
 */
public final class BytesXor {
    private BytesXor() {
    }

    /**
     * Выполняет поэлементный XOR двух массивов одинаковой длины.
     * @param a левый массив
     * @param b правый массив
     * @return новый массив c = a XOR b
     */
    public static byte[] xor(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Длины массивов должны совпадать");
        }
        byte[] r = Arrays.copyOf(a, a.length);
        for (int i = 0; i < r.length; i++) {
            r[i] = (byte) (r[i] ^ b[i]);
        }
        return r;
    }

    /**
     * Выполняет поэлементный XOR массива-накопителя с добавляемым массивом.
     * Массив accumulator изменяется на месте.
     * @param accumulator массив-накопитель (будет модифицирован)
     * @param addend добавляемый массив той же длины
     */
    public static void xorInPlace(byte[] accumulator, byte[] addend) {
        if (accumulator.length != addend.length) {
            throw new IllegalArgumentException("Длины массивов должны совпадать");
        }
        for (int i = 0; i < accumulator.length; i++) {
            accumulator[i] = (byte) (accumulator[i] ^ addend[i]);
        }
    }

    /**
     * Создаёт нулевой вектор заданной длины.
     * @param length длина
     * @return массив из нулей
     */
    public static byte[] zeros(int length) {
        return new byte[length];
    }
}
