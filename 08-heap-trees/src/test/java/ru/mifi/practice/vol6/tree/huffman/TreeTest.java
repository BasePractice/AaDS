package ru.mifi.practice.vol6.tree.huffman;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@DisplayName("Код Хаффмана")
final class TreeTest {

    @DisplayName("Восстанавливает исходный текст")
    @Test
    @Timeout(1)
    void restoresTheOriginalText() {
        assertThat("decompression dont restore the text",
            Tree.compress("абракадабра").decompress(), is("абракадабра"));
    }

    @DisplayName("Восстанавливает текст из одного символа")
    @Test
    @Timeout(1)
    void restoresSingleCharacterText() {
        assertThat("a single character text dont survive the round trip",
            Tree.compress("а").decompress(), is("а"));
    }

    @DisplayName("Восстанавливает текст из повторов одного символа")
    @Test
    @Timeout(1)
    void restoresRepeatedSingleCharacterText() {
        assertThat("an alphabet of one symbol produces an empty code and loses the data",
            Tree.compress("аааа").decompress(), is("аааа"));
    }

    @DisplayName("Не пишет литеральный null в поток")
    @Test
    @Timeout(1)
    void neverEncodesTheWordNull() {
        assertThat("a missing code leaks the literal string null into the stream",
            Tree.compress("аааа").text(), not(is("nullnullnullnull")));
    }

    @DisplayName("Частый символ получает код не длиннее редкого")
    @Test
    @Timeout(1)
    void givesTheFrequentSymbolAShorterCode() {
        assertThat("the frequent symbol dont get the shorter code",
            Tree.compress("ааааааб").text().length() < 14, is(true));
    }
}
