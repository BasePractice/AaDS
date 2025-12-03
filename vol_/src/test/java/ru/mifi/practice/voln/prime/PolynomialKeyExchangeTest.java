package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Обмен ключами на полиномах")
class PolynomialKeyExchangeTest {
    @Test
    @DisplayName("Демо GF(2)[x]/(x^3+x+1), g=x+1, совпадение секретов")
    void demoSharedSecret() {
        // Несколько пар (a, b) должны давать одинаковый общий секрет при перестановке a и b
        int[][] pairs = new int[][]{
                {1, 1}, {2, 3}, {4, 5}, {6, 7}, {8, 2}
        };
        for (int[] pair : pairs) {
            int a = pair[0];
            int b = pair[1];
            int[] s1 = PolynomialKeyExchange.demoSharedSecretGF2(a, b);
            int[] s2 = PolynomialKeyExchange.demoSharedSecretGF2(b, a);
            assertArrayEquals(s1, s2, "Секреты должны совпадать для (" + a + "," + b + ")");
        }
    }

    @Test
    @DisplayName("Отрицательная степень запрещена")
    void negativeExponent() {
        PolynomialField f = new PolynomialField(2, new int[]{1, 1, 0, 1});
        assertThrows(IllegalArgumentException.class, () -> PolynomialKeyExchange.deriveSharedSecret(f, new int[]{1, 1}, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> PolynomialKeyExchange.deriveSharedSecret(f, new int[]{1, 1}, 1, -1));
    }

    @Test
    @DisplayName("Базовые операции поля: умножение и степень")
    void fieldBasics() {
        PolynomialField f = new PolynomialField(2, new int[]{1, 1, 0, 1}); // x^3 + x + 1
        int[] x = new int[]{0, 1};
        // (x)*(x) = x^2
        assertArrayEquals(new int[]{0, 0, 1}, f.mul(x, x));
        // (x+1)^2 = x^2 + 1 в GF(2)
        int[] x1 = new int[]{1, 1};
        assertArrayEquals(new int[]{1, 0, 1}, f.pow(x1, 2));
        // (x^3) == x + 1 в поле, т.к. x^3 = x + 1 (из x^3 + x + 1 = 0)
        int[] x3 = new int[]{0, 0, 0, 1};
        assertArrayEquals(new int[]{1, 1}, f.pow(x, 3));
        // (x^3)*(x) == (x+1)*x = x^2 + x
        assertArrayEquals(new int[]{0, 1, 1}, f.mul(f.pow(x, 3), x));
        // (x+1)^0 = 1
        int[] one = new int[]{1};
        assertArrayEquals(one, f.pow(x1, 0));
    }
}
