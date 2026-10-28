package ru.mifi.practice.vol7.fibonacci;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

/**
 * Проверка трёх реализаций чисел Фибоначчи.
 *
 * <p>Нумерация здесь сдвинута: F(0) = F(1) = 1, поэтому F(10) равно 89, а не 55. Важно, что все
 * три реализации считают одно и то же — иначе сравнивать их по числу операций бессмысленно.
 */
@DisplayName("Числа Фибоначчи")
final class FibonacciTest {

    @DisplayName("Табличный счёт даёт десятое число")
    @Test
    @Timeout(1)
    void countsTheTenthNumberInATable() {
        assertThat("the table dont give the tenth number",
            new Fibonacci.Dynamited().fibonacci(10, Counter.create()), is(BigInteger.valueOf(89)));
    }

    @DisplayName("Счёт с памятью совпадает с табличным")
    @Test
    @Timeout(1)
    void agreesWithTheTableWhenMemorized() {
        assertThat("memorized counting disagrees with the table",
            new Fibonacci.Memorized().fibonacci(20, Counter.create()),
            is(new Fibonacci.Dynamited().fibonacci(20, Counter.create())));
    }

    @DisplayName("Рекурсивный счёт совпадает с табличным")
    @Test
    @Timeout(1)
    void agreesWithTheTableWhenRecursive() {
        assertThat("recursive counting disagrees with the table",
            new Fibonacci.Recursion().fibonacci(20, Counter.create()),
            is(new Fibonacci.Dynamited().fibonacci(20, Counter.create())));
    }

    @DisplayName("Память сокращает перебор на порядок")
    @Test
    @Timeout(1)
    void savesStepsByRemembering() {
        Counter memorized = Counter.create();
        new Fibonacci.Memorized().fibonacci(25, memorized);
        Counter recursive = Counter.create();
        new Fibonacci.Recursion().fibonacci(25, recursive);
        assertThat("remembering dont save an order of magnitude of steps",
            memorized.count() * 10, is(lessThan(recursive.count())));
    }

    @DisplayName("Нулевое число равно единице")
    @Test
    @Timeout(1)
    void takesOneAsTheZeroNumber() {
        assertThat("the zero number is not one",
            new Fibonacci.Dynamited().fibonacci(0, Counter.create()), is(BigInteger.ONE));
    }
}
