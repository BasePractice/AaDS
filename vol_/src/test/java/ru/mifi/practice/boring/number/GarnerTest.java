package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Восстановление числа по остаткам алгоритмом Гарнера")
final class GarnerTest {

    @DisplayName("Восстанавливает число по трём остаткам")
    @Test
    @Timeout(1)
    void restoresTheNumberFromThreeRemainders() {
        assertThat("the restored number is wrong",
            new Garner().restore(List.of(2L, 3L, 2L), List.of(3L, 5L, 7L)), is(23L));
    }

    @DisplayName("Ноль восстанавливается нулём")
    @Test
    @Timeout(1)
    void restoresZero() {
        assertThat("zero is restored as something else",
            new Garner().restore(List.of(0L, 0L), List.of(3L, 5L)), is(0L));
    }

    @DisplayName("Один модуль даёт сам остаток")
    @Test
    @Timeout(1)
    void restoresTheRemainderItself() {
        assertThat("the single remainder is changed",
            new Garner().restore(List.of(4L), List.of(7L)), is(4L));
    }

    @DisplayName("Числа модулей и остатков должны совпадать")
    @Test
    @Timeout(1)
    void refusesTheUnevenInput() {
        assertThat("the uneven input is accepted",
            assertThrows(IllegalArgumentException.class,
                () -> new Garner().restore(List.of(1L), List.of(3L, 5L))).getMessage(),
            is("Остатков и модулей должно быть поровну"));
    }

    @DisplayName("Не взаимно простые модули отвергаются")
    @Test
    @Timeout(1)
    void refusesTheCommonDivisor() {
        assertThat("the modules sharing a divisor are accepted",
            assertThrows(IllegalArgumentException.class,
                () -> new Garner().restore(List.of(1L, 1L), List.of(4L, 6L))).getMessage(),
            is("Модули 4 и 6 не взаимно просты"));
    }

    @DisplayName("Восстанавливает случайные числа по остаткам")
    @Test
    @Timeout(10)
    void restoresRandomNumbers() {
        Random random = new Random(20260815L);
        List<Long> modules = List.of(3L, 5L, 7L, 11L, 13L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 500; attempt++) {
            long number = random.nextInt(15_015);
            List<Long> remainders = modules.stream().map(module -> number % module).toList();
            if (new Garner().restore(remainders, modules) != number) {
                ++mismatches;
            }
        }
        assertThat("a random number is not restored from its remainders", mismatches, is(0));
    }
}
