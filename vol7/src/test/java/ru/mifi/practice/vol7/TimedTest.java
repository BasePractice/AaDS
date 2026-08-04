package ru.mifi.practice.vol7;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка замера времени: обёртка не должна менять поведение вычисления. */
@DisplayName("Замер времени вычисления")
final class TimedTest {

    @DisplayName("Результат вычисления доходит до вызывающего")
    @Test
    @Timeout(5)
    void givesBackTheResultOfTheComputation() {
        assertThat("the result of the computation dont reach the caller",
            new Timed.Elapsed<Integer, Integer>().timed("удвоение", 21, value -> value * 2), is(42));
    }

    @DisplayName("Вычисление запускается ровно один раз")
    @Test
    @Timeout(5)
    void runsTheComputationExactlyOnce() {
        AtomicInteger runs = new AtomicInteger();
        new Timed.Elapsed<Integer, Integer>().timed("счёт", 1, value -> runs.incrementAndGet());
        assertThat("the computation runs a different number of times", runs.get(), is(1));
    }

    @DisplayName("Аргумент доходит до вычисления")
    @Test
    @Timeout(5)
    void passesTheArgumentToTheComputation() {
        assertThat("the argument dont reach the computation",
            new Timed.Elapsed<String, String>().timed("эхо", "привет", value -> value), is("привет"));
    }

    /** Замер стоит в finally, поэтому отказ вычисления обязан пройти наружу нетронутым. */
    @DisplayName("Отказ вычисления проходит наружу")
    @Test
    @Timeout(5)
    void letsTheFailureThrough() {
        assertThrows(IllegalStateException.class,
            () -> new Timed.Elapsed<Integer, Integer>().timed("отказ", 1, value -> {
                throw new IllegalStateException("вычисление не удалось");
            }),
            "the failure of the computation gets swallowed by the measurement");
    }
}
