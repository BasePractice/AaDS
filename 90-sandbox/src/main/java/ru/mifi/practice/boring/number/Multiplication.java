package ru.mifi.practice.boring.number;

/**
 * Умножение длинных чисел. Реализаций две: столбиком, как учат в школе, и по Карацубе. Контракт
 * общий, поэтому их можно сверять между собой и мерить на одних и тех же числах — разница O(n²)
 * против O(n^1.585) видна уже на нескольких сотнях цифр.
 */
@FunctionalInterface
public interface Multiplication {
    Digits product(Digits left, Digits right);
}
