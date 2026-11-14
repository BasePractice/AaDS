package ru.mifi.practice.vol2.recurs;

import java.math.BigInteger;

import ru.mifi.practice.commons.Counter;

/** Место для решения студента. Возведение в степень с делением показателя пополам. */
public final class FastPower {
    /**
     * Возвращает точную степень целого основания. При нулевом показателе возвращает 1,
     * в том числе для нулевого основания. Счётчик увеличивается на каждое умножение.
     *
     * @throws IllegalArgumentException если показатель отрицателен
     */
    public BigInteger power(BigInteger base, int exponent, Counter counter) {
        throw new UnsupportedOperationException("Реализуйте возведение в степень с делением показателя пополам");
    }
}
