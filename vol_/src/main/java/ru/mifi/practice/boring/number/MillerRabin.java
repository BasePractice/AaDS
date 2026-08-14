package ru.mifi.practice.boring.number;

import java.math.BigInteger;

/**
 * Тест Миллера — Рабина: проверяет простоту за десяток возведений в степень по модулю вместо
 * перебора делителей. На нём держится вся практическая криптография — ключи RSA начинаются с
 * поиска больших простых чисел, а перебирать делители у числа из шестисот цифр невозможно.
 *
 * <p>Идея от малой теоремы Ферма: если p простое, то a^(p−1) ≡ 1 по модулю p. Обратное неверно,
 * и числа Кармайкла обманывают проверку Ферма при любом основании. Миллер и Рабин усилили её:
 * число p−1 раскладывается как d·2^s, и для простого p последовательность a^d, a^2d, a^4d, ...
 * обязана прийти к единице, причём прийти именно через минус единицу, а не как попало. Составное
 * число это условие выдаёт.
 *
 * <p>Одно основание ошибается не чаще чем в четверти случаев, поэтому оснований берут несколько.
 * Здесь взят набор из семи первых простых: для чисел, помещающихся в long, он даёт точный ответ —
 * это проверено полным перебором, и вероятностным тест остаётся только формально.
 */
public final class MillerRabin implements Primality {
    private static final long[] WITNESSES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    @Override
    public boolean prime(long number) {
        if (number < 2) {
            return false;
        }
        for (long witness : WITNESSES) {
            if (number % witness == 0) {
                return number == witness;
            }
        }
        long odd = number - 1;
        int twos = 0;
        while (odd % 2 == 0) {
            odd /= 2;
            ++twos;
        }
        for (long witness : WITNESSES) {
            if (composite(number, witness, odd, twos)) {
                return false;
            }
        }
        return true;
    }

    /** Свидетельство составности: последовательность квадратов не пришла к единице как надо. */
    private boolean composite(long number, long witness, long odd, int twos) {
        BigInteger modulus = BigInteger.valueOf(number);
        BigInteger value = BigInteger.valueOf(witness).modPow(BigInteger.valueOf(odd), modulus);
        if (value.equals(BigInteger.ONE) || value.equals(modulus.subtract(BigInteger.ONE))) {
            return false;
        }
        for (int step = 1; step < twos; step++) {
            value = value.multiply(value).mod(modulus);
            if (value.equals(modulus.subtract(BigInteger.ONE))) {
                return false;
            }
        }
        return true;
    }
}
