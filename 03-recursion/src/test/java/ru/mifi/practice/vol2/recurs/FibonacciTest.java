package ru.mifi.practice.vol2.recurs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Проверка рекурсивных чисел Фибоначчи.
 *
 * <p>Нумерация сдвинута: F(0) = F(1) = 1, поэтому F(10) равно 89, а не 55. Это та же нумерация,
 * что и у реализаций из раздела динамического программирования, — иначе сравнивать их по числу
 * шагов было бы не с чем.
 */
@DisplayName("Числа Фибоначчи рекурсией")
final class FibonacciTest {

    @DisplayName("Нулевое число равно единице")
    @Test
    @Timeout(1)
    void takesOneAsTheZeroNumber() {
        assertThat("the zero number is not one", new Fibonacci().fibonacci(0), is(1));
    }

    @DisplayName("Первое число равно единице")
    @Test
    @Timeout(1)
    void takesOneAsTheFirstNumber() {
        assertThat("the first number is not one", new Fibonacci().fibonacci(1), is(1));
    }

    @DisplayName("Каждое число — сумма двух предыдущих")
    @Test
    @Timeout(5)
    void sumsTheTwoPreviousNumbers() {
        Fibonacci fibonacci = new Fibonacci();
        boolean broken = false;
        for (int n = 2; n <= 20; n++) {
            broken |= fibonacci.fibonacci(n) != fibonacci.fibonacci(n - 1) + fibonacci.fibonacci(n - 2);
        }
        assertThat("a number is not the sum of the two previous ones", broken, is(false));
    }

    @DisplayName("Десятое число равно восьмидесяти девяти")
    @Test
    @Timeout(5)
    void countsTheTenthNumber() {
        assertThat("the tenth number is wrong", new Fibonacci().fibonacci(10), is(89));
    }
}
