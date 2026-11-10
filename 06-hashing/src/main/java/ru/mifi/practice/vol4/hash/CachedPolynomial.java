package ru.mifi.practice.vol4.hash;

import ru.mifi.practice.commons.Counter;

import java.util.Arrays;

import static ru.mifi.practice.vol4.hash.Hash.POLY_MOD;
import static ru.mifi.practice.vol4.hash.Hash.POLY_PRIME;

/** Хранит степени основания и строит префиксные хеши строк. */
final class CachedPolynomial {
    static final int CACHED_POLYNOMIALS = Character.MAX_VALUE;
    /** Вес позиции i равен base в степени i по модулю mod. Первый вес равен 1. */
    final int[] polynomials;
    final int mod;
    private final int base;

    CachedPolynomial(int mod, int k) {
        this.mod = mod;
        this.base = k;
        this.polynomials = new int[CACHED_POLYNOMIALS];
        this.polynomials[0] = 1;
        fillPowers(polynomials, 1);
    }

    CachedPolynomial() {
        this(POLY_MOD, POLY_PRIME);
    }

    /**
     * Возвращает хеш участка от left до right включительно за O(1).
     * Из хеша префикса до right вычитается вклад символов перед left.
     * Веса остаются привязаны к позициям в исходном тексте.
     * Отрицательная разность приводится к остатку от 0 до mod − 1.
     */
    int hash(int[] hashes, int left, int right) {
        if (hashes.length == 0 || right < left) {
            return 0;
        }
        long result = hashes[right];
        if (left > 0) {
            result -= hashes[left - 1];
        }
        return (int) ((result % mod + mod) % mod);
    }

    /** Возвращает кэш весов или расширяет его копию для длинной строки. */
    int[] powers(int length) {
        if (length <= polynomials.length) {
            return polynomials;
        }
        int[] powers = Arrays.copyOf(polynomials, length);
        fillPowers(powers, polynomials.length);
        return powers;
    }

    /** Получает очередной вес умножением предыдущего на основание по модулю mod. */
    private void fillPowers(int[] powers, int start) {
        for (int i = start; i < powers.length; i++) {
            powers[i] = (int) ((long) powers[i - 1] * base % mod);
        }
    }

    int[] hashing(String text, Counter counter) {
        return hashing(text, powers(text.length()), counter);
    }

    /**
     * Элемент с индексом i хранит хеш первых i + 1 символов.
     * К предыдущему хешу прибавляется код очередного символа, умноженный на его вес.
     */
    int[] hashing(String text, int[] powers, Counter counter) {
        int[] hash = new int[text.length()];
        if (text.isEmpty()) {
            return hash;
        }
        hash[0] = text.charAt(0) % mod;
        counter.increment();
        for (int i = 1; i < text.length(); i++) {
            hash[i] = (int) ((hash[i - 1] + (long) powers[i] * text.charAt(i)) % mod);
            counter.increment();
        }
        return hash;
    }
}
