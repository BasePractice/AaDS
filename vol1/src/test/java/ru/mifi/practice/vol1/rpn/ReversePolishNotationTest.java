package ru.mifi.practice.vol1.rpn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Обратная польская запись")
final class ReversePolishNotationTest {

    @DisplayName("Складывает два операнда")
    @Test
    @Timeout(1)
    void addsTwoOperands() {
        assertThat("addition dont produce the sum",
            new ReversePolishNotation.Default().evaluate("3 2 +").doubleValue(), closeTo(5, 1e-9));
    }

    @DisplayName("Вычитает второй операнд из первого")
    @Test
    @Timeout(1)
    void subtractsSecondOperandFromFirst() {
        assertThat("subtraction takes the operands in reverse order",
            new ReversePolishNotation.Default().evaluate("3 2 -").doubleValue(), closeTo(1, 1e-9));
    }

    @DisplayName("Делит первый операнд на второй")
    @Test
    @Timeout(1)
    void dividesFirstOperandBySecond() {
        assertThat("division takes the operands in reverse order",
            new ReversePolishNotation.Default().evaluate("10 2 /").doubleValue(), closeTo(5, 1e-9));
    }

    @DisplayName("Умножает два операнда")
    @Test
    @Timeout(1)
    void multipliesTwoOperands() {
        assertThat("multiplication dont produce the product",
            new ReversePolishNotation.Default().evaluate("3 2 *").doubleValue(), closeTo(6, 1e-9));
    }

    @DisplayName("Вычисляет вложенное выражение слева направо")
    @Test
    @Timeout(1)
    void evaluatesNestedExpressionLeftToRight() {
        assertThat("nested expression dont fold left to right",
            new ReversePolishNotation.Default().evaluate("9 3 - 2 /").doubleValue(), closeTo(3, 1e-9));
    }

    @DisplayName("Берёт модуль от вершины стека")
    @Test
    @Timeout(1)
    void takesAbsoluteValueOfTheTop() {
        assertThat("abs dont drop the sign",
            new ReversePolishNotation.Default().evaluate("1 10 - abs").doubleValue(), closeTo(9, 1e-9));
    }

    @DisplayName("Пустое выражение вычислить нельзя")
    @Test
    @Timeout(1)
    void cannotEvaluateEmptyExpression() {
        ReversePolishNotation notation = new ReversePolishNotation.Default();
        assertThrows(IllegalArgumentException.class, () -> notation.evaluate(""),
            "empty expression dont fail");
    }

    @DisplayName("Неизвестную операцию вычислить нельзя")
    @Test
    @Timeout(1)
    void cannotEvaluateUnknownOperation() {
        ReversePolishNotation notation = new ReversePolishNotation.Default();
        assertThrows(UnsupportedOperationException.class, () -> notation.evaluate("1 2 ^"),
            "unknown operation dont fail");
    }
}
