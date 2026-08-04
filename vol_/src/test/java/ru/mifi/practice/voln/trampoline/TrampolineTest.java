package ru.mifi.practice.voln.trampoline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка трамплина: рекурсия разворачивается в цикл, поэтому глубина, на которой обычный
 * вызов переполнил бы стек, здесь проходит без единого лишнего кадра.
 */
@DisplayName("Трамплин")
final class TrampolineTest {

    @DisplayName("Готовый результат отдаётся сразу")
    @Test
    @Timeout(1)
    void givesTheDoneValueRightAway() {
        assertThat("the done value is not given right away", Trampoline.done(42).get(), is(42));
    }

    @DisplayName("Готовый результат никуда не прыгает")
    @Test
    @Timeout(1)
    void staysOnTheSpotWhenDone() {
        Trampoline<Integer> done = Trampoline.done(42);
        assertThat("the done trampoline jumps somewhere", done.run(), is(done));
    }

    @DisplayName("Отложенный шаг возвращает следующий прыжок")
    @Test
    @Timeout(1)
    void givesTheNextJumpWhenMore() {
        assertThat("the deferred step dont give the next jump",
            Trampoline.more(() -> Trampoline.done(42)).run().get(), is(42));
    }

    @DisplayName("У отложенного шага результата ещё нет")
    @Test
    @Timeout(1)
    void refusesTheValueWhileMoreIsPending() {
        Trampoline<Integer> more = Trampoline.more(() -> Trampoline.done(42));
        assertThrows(UnsupportedOperationException.class, more::get,
            "a pending step gives a value it dont have yet");
    }

    @DisplayName("Факториал считается на трамплине")
    @Test
    @Timeout(5)
    void countsTheFactorialOnTheTrampoline() {
        assertThat("the factorial on the trampoline is wrong",
            Main.calculateFactorial(5), is(BigInteger.valueOf(120)));
    }

    /** Глубина, на которой обычная рекурсия падает: смысл трамплина ровно в этом. */
    @DisplayName("Глубина в сто тысяч шагов не переполняет стек")
    @Test
    @Timeout(30)
    void survivesAHundredThousandSteps() {
        assertThat("a hundred thousand steps overflow the stack",
            Main.calculateFactorial(100000).signum(), is(1));
    }
}
