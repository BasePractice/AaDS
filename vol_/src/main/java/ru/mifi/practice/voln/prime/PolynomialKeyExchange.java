package ru.mifi.practice.voln.prime;

import java.util.Arrays;

/**
 * Пример обмена ключами на многочленах над полем простого модуля p.
 * Идея аналогична Диффи—Хеллману, но вместо целых по модулю используется поле GF(p)[x]/(m(x)).
 */
public final class PolynomialKeyExchange {
    private PolynomialKeyExchange() {
    }

    /**
     * Вычисляет общий секрет для секретных степеней a и b при фиксированных параметрах поля и базового полинома g(x).
     * Возвращает коэффициенты общего секрета по возрастанию степеней.
     *
     * @param field поле GF(p)[x]/(m(x))
     * @param base  базовый полином g(x)
     * @param a     секрет участника A (неотрицательная степень)
     * @param b     секрет участника B (неотрицательная степень)
     * @return общий секрет в виде массива коэффициентов
     */
    public static int[] deriveSharedSecret(PolynomialField field, int[] base, int a, int b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Степени должны быть неотрицательными");
        }
        int[] gA = field.pow(base, a);
        int[] gAB = field.pow(gA, b); // (g^a)^b
        // Для надежности вернём нормализованный результат (обрезка нулей в старших коэффициентах)
        return Arrays.copyOf(gAB, gAB.length);
    }

    /**
     * Демонстрационная конфигурация: поле GF(2)[x]/(x^3 + x + 1) и базовый полином g(x) = x + 1.
     * Пара значений (a, b) возвращает общий секрет.
     *
     * @param a секрет участника A
     * @param b секрет участника B
     * @return общий секрет как массив коэффициентов по возрастанию степеней
     */
    public static int[] demoSharedSecretGF2(int a, int b) {
        int p = 2;
        // модуль x^3 + x + 1 => коэффициенты: 1 (x^0), 1 (x^1), 0 (x^2), 1 (x^3)
        PolynomialField field = new PolynomialField(p, new int[]{1, 1, 0, 1});
        // базовый полином g(x) = x + 1 => [1, 1]
        int[] g = new int[]{1, 1};
        return deriveSharedSecret(field, g, a, b);
    }
}
