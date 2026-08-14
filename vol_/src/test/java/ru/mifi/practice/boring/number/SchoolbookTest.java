package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Умножение длинных чисел столбиком")
final class SchoolbookTest {

    @DisplayName("Перемножает числа с переносами")
    @Test
    @Timeout(1)
    void multipliesWithCarries() {
        assertThat("the carries are handled wrong",
            new Schoolbook().product(new Digits("999"), new Digits("999")).toString(), is("998001"));
    }

    @DisplayName("Умножение на единицу ничего не меняет")
    @Test
    @Timeout(1)
    void multipliesByOne() {
        assertThat("the product with one differs from the number",
            new Schoolbook().product(new Digits("123456"), new Digits("1")).toString(), is("123456"));
    }

    @DisplayName("Умножение на ноль даёт ноль")
    @Test
    @Timeout(1)
    void multipliesByZero() {
        assertThat("the product with zero is not zero",
            new Schoolbook().product(new Digits("123456"), new Digits("0")).toString(), is("0"));
    }

    @DisplayName("Ведущие нули в ответе не остаются")
    @Test
    @Timeout(1)
    void leavesNoLeadingZeroes() {
        assertThat("the answer keeps its leading zeroes",
            new Schoolbook().product(new Digits("10"), new Digits("10")).toString(), is("100"));
    }
}
