package ru.mifi.practice.vol2.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Разбор математических выражений")
final class MathCalculatorTest {

    @DisplayName("Вычисляет одиночное число")
    @Test
    @Timeout(1)
    void evaluatesLoneNumber() {
        assertThat("lone number dont evaluate to itself",
            new MathCalculator.Simple().evaluate("2").doubleValue(), closeTo(2, 1e-9));
    }

    @DisplayName("Складывает")
    @Test
    @Timeout(1)
    void addsTwoNumbers() {
        assertThat("addition dont produce the sum",
            new MathCalculator.Simple().evaluate("2 + 4").doubleValue(), closeTo(6, 1e-9));
    }

    @DisplayName("Умножает")
    @Test
    @Timeout(1)
    void multipliesTwoNumbers() {
        assertThat("multiplication dont produce the product",
            new MathCalculator.Simple().evaluate("2 * 4").doubleValue(), closeTo(8, 1e-9));
    }

    @DisplayName("Умножает три множителя подряд")
    @Test
    @Timeout(1)
    void multipliesThreeFactorsInARow() {
        assertThat("a chain of multiplications stops after the second factor",
            new MathCalculator.Simple().evaluate("2 * 3 * 4").doubleValue(), closeTo(24, 1e-9));
    }

    @DisplayName("Складывает три слагаемых подряд")
    @Test
    @Timeout(1)
    void addsThreeTermsInARow() {
        assertThat("a chain of additions dont fold completely",
            new MathCalculator.Simple().evaluate("2 + 4 + 15").doubleValue(), closeTo(21, 1e-9));
    }

    @DisplayName("Умножение приоритетнее сложения")
    @Test
    @Timeout(1)
    void bindsMultiplicationTighterThanAddition() {
        assertThat("multiplication dont bind tighter than addition",
            new MathCalculator.Simple().evaluate("2 + 4 * 15").doubleValue(), closeTo(62, 1e-9));
    }

    @DisplayName("Скобки меняют приоритет")
    @Test
    @Timeout(1)
    void letsBracketsOverridePriority() {
        assertThat("brackets dont override the priority",
            new MathCalculator.Simple().evaluate("( 2 + 4 ) * 15").doubleValue(), closeTo(90, 1e-9));
    }

    @DisplayName("Разбирает вложенные скобки")
    @Test
    @Timeout(1)
    void evaluatesNestedBrackets() {
        assertThat("nested brackets dont evaluate",
            new MathCalculator.Simple().evaluate("( 4 * ( 3 + 7 ) ) * 3").doubleValue(), closeTo(120, 1e-9));
    }

    @DisplayName("Разбирает многозначные числа")
    @Test
    @Timeout(1)
    void readsMultiDigitNumbers() {
        assertThat("multi digit number dont survive the parser",
            new MathCalculator.Simple().evaluate("123 + 1").doubleValue(), closeTo(124, 1e-9));
    }

    @DisplayName("Хвост после выражения разобрать нельзя")
    @Test
    @Timeout(1)
    void cannotLeaveTrailingInput() {
        MathCalculator calculator = new MathCalculator.Simple();
        assertThrows(IllegalArgumentException.class, () -> calculator.evaluate("2 34"),
            "trailing input is silently dropped");
    }

    @DisplayName("Незакрытую скобку разобрать нельзя")
    @Test
    @Timeout(1)
    void cannotParseUnclosedBracket() {
        MathCalculator calculator = new MathCalculator.Simple();
        assertThrows(IllegalArgumentException.class, () -> calculator.evaluate("( 2 + 4"),
            "unclosed bracket dont fail");
    }
}
