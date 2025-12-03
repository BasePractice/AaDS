package ru.mifi.practice.voln.codes;

import lombok.experimental.UtilityClass;

/**
 * Утилиты для операций XOR над массивами байтов (GF(2)).
 */
@UtilityClass
public final class BytesXor {
    /**
     * Выполняет поэлементный XOR массива-накопителя с добавляемым массивом.
     * Массив accumulator изменяется на месте.
     *
     * @param accumulator массив-накопитель (будет модифицирован)
     * @param addend      добавляемый массив той же длины
     */
    public void xorInPlace(byte[] accumulator, byte[] addend) {
        if (accumulator.length != addend.length) {
            throw new IllegalArgumentException("Длины массивов должны совпадать");
        }
        for (int i = 0; i < accumulator.length; i++) {
            accumulator[i] = (byte) (accumulator[i] ^ addend[i]);
        }
    }

    /**
     * Создаёт нулевой вектор заданной длины.
     *
     * @param length длина
     * @return массив из нулей
     */
    public byte[] zeros(int length) {
        return new byte[length];
    }
}
