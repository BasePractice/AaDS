package ru.mifi.practice.boring.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Преобразование Барроуза — Уилера")
final class BurrowsWheelerTest {

    @DisplayName("Восстанавливает преобразованный текст")
    @Test
    @Timeout(1)
    void restoresTheTransformedText() {
        BurrowsWheeler wheeler = new BurrowsWheeler();
        assertThat("the text dont survive the round trip",
            wheeler.restore(wheeler.transform("абракадабра")), is("абракадабра"));
    }

    @DisplayName("Длина текста не меняется")
    @Test
    @Timeout(1)
    void keepsTheLength() {
        assertThat("the transform changes the length of the text",
            new BurrowsWheeler().transform("абракадабра").length(), is(12));
    }

    @DisplayName("Сбивает одинаковые буквы в группы")
    @Test
    @Timeout(1)
    void groupsTheEqualLetters() {
        assertThat("the equal letters are left scattered",
            new BurrowsWheeler().transform("bananabanana").contains("nnnn"), is(true));
    }

    @DisplayName("Пустой текст переживает преобразование")
    @Test
    @Timeout(1)
    void survivesTheEmptyText() {
        BurrowsWheeler wheeler = new BurrowsWheeler();
        assertThat("the empty text breaks the transform", wheeler.restore(wheeler.transform("")), is(""));
    }

    @DisplayName("Текст со служебным маркером отвергается сразу")
    @Test
    @Timeout(1)
    void refusesTheTextWithTheMarker() {
        assertThat("a text with the service marker is transformed silently",
            assertThrows(IllegalArgumentException.class,
                () -> new BurrowsWheeler().transform("a\u0003b")).getMessage(),
            is("Текст содержит служебный маркер конца строки"));
    }

    @DisplayName("Восстанавливает случайные тексты")
    @Test
    @Timeout(10)
    void restoresRandomTexts() {
        Random random = new Random(20260815L);
        BurrowsWheeler wheeler = new BurrowsWheeler();
        int mismatches = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < random.nextInt(50); i++) {
                text.append((char) ('a' + random.nextInt(3)));
            }
            if (!wheeler.restore(wheeler.transform(text.toString())).contentEquals(text)) {
                ++mismatches;
            }
        }
        assertThat("a random text dont survive the round trip", mismatches, is(0));
    }
}
