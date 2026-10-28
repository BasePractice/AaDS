package ru.mifi.practice.vol2.recurs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка факториала в двух видах: рекурсией и циклом. */
@DisplayName("Факториал")
final class FactorialTest {

    @DisplayName("Факториал нуля равен единице")
    @Test
    @Timeout(1)
    void takesOneAsTheFactorialOfZero() {
        assertThat("the factorial of zero is not one", new Factorial(0).recursiveFactorial(), is(1));
    }

    @DisplayName("Факториал пяти равен ста двадцати")
    @Test
    @Timeout(1)
    void countsTheFactorialOfFive() {
        assertThat("the factorial of five is wrong", new Factorial(5).recursiveFactorial(), is(120));
    }

    @DisplayName("Цикл даёт то же, что и рекурсия")
    @Test
    @Timeout(1)
    void agreesBetweenTheLoopAndTheRecursion() {
        assertThat("the loop disagrees with the recursion",
            new Factorial(10).iterationFactorial(), is(new Factorial(10).recursiveFactorial()));
    }

    @DisplayName("Факториал отрицательного вырождается в единицу")
    @Test
    @Timeout(1)
    void takesOneForANegativeArgument() {
        assertThat("a negative argument gives something other than one",
            new Factorial(-3).recursiveFactorial(), is(1));
    }

    /**
     * Двенадцать — последнее значение, влезающее в int. Дальше обе реализации переполняются
     * одинаково, и это стоит видеть: разрядность здесь ограничивает раньше, чем глубина стека.
     */
    @DisplayName("Двенадцать — последний факториал, влезающий в разрядность")
    @Test
    @Timeout(1)
    void reachesTheLimitOfTheIntegerRange() {
        assertThat("the last factorial fitting into an int is wrong",
            new Factorial(12).recursiveFactorial(), is(479001600));
    }
}
