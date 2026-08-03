package ru.mifi.practice.vol4.hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mifi.practice.vol4.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Полиномиальный хеш")
final class HashTest {

    @DisplayName("Одинаковый текст даёт одинаковый хеш")
    @Test
    void givesTheSameHashForTheSameText() {
        Hash hash = new Hash.PolynomialHash();
        assertThat("the same text hashes differently on a second call",
            hash.hash("алгоритмы", new Counter.Default()), is(hash.hash("алгоритмы", new Counter.Default())));
    }

    @DisplayName("Хеш длинного текста остаётся неотрицательным")
    @Test
    void keepsTheHashNonNegativeOnLongText() {
        assertThat("modular arithmetic overflows int and produces a negative hash",
            new Hash.PolynomialHash().hash("абвгдеёжзийклмнопрстуфхцчшщъыьэюя", new Counter.Default()),
            greaterThanOrEqualTo(0));
    }

    @DisplayName("Хеш не выходит за модуль")
    @Test
    void keepsTheHashBelowTheModulus() {
        assertThat("hash escapes the modulus",
            new Hash.PolynomialHash().hash("абвгдеёжзийклмнопрстуфхцчшщъыьэюя", new Counter.Default()),
            lessThan(Hash.POLY_MOD));
    }

    @DisplayName("Кешированный хеш длинного текста остаётся неотрицательным")
    @Test
    void keepsTheCachedHashNonNegativeOnLongText() {
        assertThat("cached powers overflow int and produce a negative hash",
            new Hash.PolynomialHashCached().hash("абвгдеёжзийклмнопрстуфхцчшщъыьэюя", new Counter.Default()),
            greaterThanOrEqualTo(0));
    }

    @DisplayName("Кешированный хеш пустого текста равен нулю")
    @Test
    void hashesEmptyTextToZero() {
        assertThat("empty text dont hash to zero",
            new Hash.PolynomialHashCached().hash("", new Counter.Default()), is(0));
    }
}
