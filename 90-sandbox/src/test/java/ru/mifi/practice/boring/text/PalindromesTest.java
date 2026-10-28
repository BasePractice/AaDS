package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Палиндромы по Манакеру")
final class PalindromesTest {

    @DisplayName("Находит нечётный палиндром")
    @Test
    @Timeout(1)
    void findsTheOddPalindrome() {
        assertThat("the odd palindrome is missed", new Palindromes("abacaba").longest(), is("abacaba"));
    }

    @DisplayName("Находит чётный палиндром")
    @Test
    @Timeout(1)
    void findsTheEvenPalindrome() {
        assertThat("the even palindrome is missed", new Palindromes("xabbay").longest(), is("abba"));
    }

    @DisplayName("Находит палиндром внутри строки")
    @Test
    @Timeout(1)
    void findsThePalindromeInsideTheText() {
        assertThat("the palindrome inside the text is missed",
            new Palindromes("forgeeksskeegfor").longest(), is("geeksskeeg"));
    }

    @DisplayName("В строке без палиндромов берётся одна буква")
    @Test
    @Timeout(1)
    void takesASingleLetterWhenNothingIsSymmetric() {
        assertThat("a text without palindromes gives more than a letter",
            new Palindromes("abcd").longest(), is("a"));
    }

    @DisplayName("Считает все палиндромы, включая однобуквенные")
    @Test
    @Timeout(1)
    void countsEveryPalindrome() {
        assertThat("the palindromes of the text are miscounted", new Palindromes("aba").count(), is(4));
    }

    @DisplayName("Считает палиндромы строки из одинаковых букв")
    @Test
    @Timeout(1)
    void countsThePalindromesOfARepeatedLetter() {
        assertThat("the palindromes of a repeated letter are miscounted",
            new Palindromes("aaa").count(), is(6));
    }
}
