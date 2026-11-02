package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Числа Стирлинга")
final class StirlingTest {

    @DisplayName("Второго рода: пять элементов на две части")
    @Test
    @Timeout(1)
    void countsSecondKindPartitions() {
        assertThat("the second kind Stirling number dont equal 15",
            Stirling.second(5, 2), is(BigInteger.valueOf(15)));
    }

    @DisplayName("Первого рода: пять элементов и два цикла")
    @Test
    @Timeout(1)
    void countsFirstKindCycles() {
        assertThat("the first kind Stirling number dont equal 50",
            Stirling.first(5, 2), is(BigInteger.valueOf(50)));
    }

    @DisplayName("Разбиение пустого множества на пустое число частей одно")
    @Test
    @Timeout(1)
    void countsEmptyPartition() {
        assertThat("an empty set dont have exactly one empty partition",
            Stirling.second(0, 0), is(BigInteger.ONE));
    }

    @DisplayName("Отрицательные аргументы отвергаются")
    @Test
    @Timeout(1)
    void cannotCountNegativeArguments() {
        assertThrows(IllegalArgumentException.class, () -> Stirling.second(-1, 0),
            "a negative Stirling argument is accepted");
    }
}
