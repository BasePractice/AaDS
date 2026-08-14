package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Фонетический код Soundex")
final class SoundexTest {

    @DisplayName("Кодирует фамилию буквой и тремя цифрами")
    @Test
    @Timeout(1)
    void codesTheNameWithALetterAndThreeDigits() {
        assertThat("the soundex code of Robert is wrong", new Soundex().code("Robert"), is("R163"));
    }

    @DisplayName("Одинаково звучащие фамилии получают один код")
    @Test
    @Timeout(1)
    void givesOneCodeToTheNamesThatSoundAlike() {
        assertThat("Rupert and Robert get different codes",
            new Soundex().code("Rupert"), is(new Soundex().code("Robert")));
    }

    @DisplayName("Гласная разделяет одинаковые цифры")
    @Test
    @Timeout(1)
    void keepsTheDigitsSeparatedByAVowel() {
        assertThat("the vowel dont separate the equal digits", new Soundex().code("Tymczak"), is("T522"));
    }

    @DisplayName("Буква H одинаковые цифры не разделяет")
    @Test
    @Timeout(1)
    void mergesTheDigitsSeparatedByH() {
        assertThat("the letter h separates the equal digits", new Soundex().code("Ashcraft"), is("A261"));
    }

    @DisplayName("Короткая фамилия добивается нулями")
    @Test
    @Timeout(1)
    void padsTheShortNameWithZeroes() {
        assertThat("the short name is not padded", new Soundex().code("Lee"), is("L000"));
    }

    @DisplayName("Слово без латинских букв кодировать нечем")
    @Test
    @Timeout(1)
    void refusesTheWordWithoutLatinLetters() {
        assertThat("a word without latin letters gets a code",
            assertThrows(IllegalArgumentException.class, () -> new Soundex().code("Иванов")).getMessage(),
            is("Слово без латинских букв фонетического кода не имеет"));
    }
}
