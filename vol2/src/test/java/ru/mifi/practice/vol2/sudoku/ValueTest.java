package ru.mifi.practice.vol2.sudoku;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Проверка содержимого клетки судоку.
 *
 * <p>Клетки сравниваются по значению, а не по ссылке: перебор с возвратом кладёт и снимает их
 * тысячами, и от равенства зависит, считается ли клетка занятой.
 */
@DisplayName("Клетка судоку")
final class ValueTest {

    @DisplayName("Пустая клетка печатается прочерком")
    @Test
    @Timeout(1)
    void printsTheEmptyCellAsADash() {
        assertThat("the empty cell is not printed as a dash", Value.EMPTY.toString(), is("-"));
    }

    @DisplayName("Клетка с цифрой печатается цифрой")
    @Test
    @Timeout(1)
    void printsTheDigitCellAsItsDigit() {
        assertThat("the digit cell is not printed as its digit", Value.DIGITS[7].toString(), is("7"));
    }

    @DisplayName("Нулевая клетка набора — пустая")
    @Test
    @Timeout(1)
    void takesTheEmptyCellAsTheZeroOne() {
        assertThat("the zero cell of the set is not the empty one", Value.DIGITS[0], is(Value.EMPTY));
    }

    @DisplayName("Одинаковые цифры равны")
    @Test
    @Timeout(1)
    void countsEqualDigitsEqual() {
        assertThat("equal digits are not equal", Value.DIGITS[3], is(Value.DIGITS[3]));
    }

    @DisplayName("Разные цифры не равны")
    @Test
    @Timeout(1)
    void separatesDifferentDigits() {
        assertThat("different digits are equal", Value.DIGITS[3], is(not(Value.DIGITS[4])));
    }

    @DisplayName("Цифра не равна пустой клетке")
    @Test
    @Timeout(1)
    void separatesADigitFromTheEmptyCell() {
        assertThat("a digit equals the empty cell", Value.DIGITS[3], is(not(Value.EMPTY)));
    }

    @DisplayName("Равные цифры дают равный хеш")
    @Test
    @Timeout(1)
    void hashesEqualDigitsAlike() {
        assertThat("equal digits give different hashes",
            Value.DIGITS[3].hashCode(), is(Value.DIGITS[3].hashCode()));
    }
}
