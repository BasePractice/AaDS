package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@DisplayName("Фонетический код Metaphone")
final class MetaphoneTest {

    @DisplayName("Сочетание ph звучит как f")
    @Test
    @Timeout(1)
    void readsPhAsF() {
        assertThat("the letters ph are not read as f", new Metaphone().code("Phone"), is("FN"));
    }

    @DisplayName("Разное написание одного звука даёт один код")
    @Test
    @Timeout(1)
    void givesOneCodeToOneSound() {
        assertThat("phone and fone get different codes",
            new Metaphone().code("Phone"), is(new Metaphone().code("Fone")));
    }

    @DisplayName("Soundex тот же звук за один не считает")
    @Test
    @Timeout(1)
    void tellsTheDifferenceWithSoundex() {
        assertThat("soundex suddenly agrees that phone and fone sound alike",
            new Soundex().code("Phone"), is(not(new Soundex().code("Fone"))));
    }

    @DisplayName("Гласные внутри слова выбрасываются")
    @Test
    @Timeout(1)
    void dropsTheInnerVowels() {
        assertThat("smith and smyth get different codes",
            new Metaphone().code("Smith"), is(new Metaphone().code("Smyth")));
    }

    @DisplayName("Начальная гласная остаётся")
    @Test
    @Timeout(1)
    void keepsTheLeadingVowel() {
        assertThat("the leading vowel is dropped", new Metaphone().code("Ann"), is("AN"));
    }

    @DisplayName("Сдвоенные буквы читаются как одна")
    @Test
    @Timeout(1)
    void readsTheDoubledLetterAsOne() {
        assertThat("the doubled letter is read twice", new Metaphone().code("Bell"), is("BL"));
    }
}
