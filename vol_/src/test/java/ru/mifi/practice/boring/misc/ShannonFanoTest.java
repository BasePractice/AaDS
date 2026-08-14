package ru.mifi.practice.boring.misc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Префиксный код Шеннона — Фано")
final class ShannonFanoTest {

    @DisplayName("Частому символу достаётся код не длиннее, чем редкому")
    @Test
    @Timeout(1)
    void givesTheShorterCodeToTheFrequentSymbol() {
        Map<Character, String> codes = new ShannonFano().codes("aaaaaaaabbbbccd");
        assertThat("the frequent symbol got a longer code",
            codes.get('a').length(), lessThanOrEqualTo(codes.get('d').length()));
    }

    @DisplayName("Ни один код не является началом другого")
    @Test
    @Timeout(1)
    void keepsTheCodesPrefixFree() {
        Map<Character, String> codes = new ShannonFano().codes("abracadabra");
        int violations = 0;
        for (String code : codes.values()) {
            for (String other : codes.values()) {
                violations += !code.equals(other) && other.startsWith(code) ? 1 : 0;
            }
        }
        assertThat("a code is the beginning of another one", violations, is(0));
    }

    @DisplayName("Каждому символу текста находится код")
    @Test
    @Timeout(1)
    void codesEverySymbol() {
        assertThat("a symbol of the text is left without a code",
            new ShannonFano().codes("abracadabra").size(), is(5));
    }

    @DisplayName("Закодированный текст короче восьми бит на символ")
    @Test
    @Timeout(1)
    void packsTheTextTighterThanBytes() {
        assertThat("the coded text is no shorter than plain bytes",
            new ShannonFano().length("aaaaaaaabbbbccd"), lessThan(15 * 8));
    }

    @DisplayName("Текст из одного символа кодируется одним битом на символ")
    @Test
    @Timeout(1)
    void codesTheSingleSymbolText() {
        assertThat("the single symbol text is coded wrong", new ShannonFano().length("aaaa"), is(0));
    }

    @DisplayName("Пустой текст кодировать нечем")
    @Test
    @Timeout(1)
    void refusesTheEmptyText() {
        assertThat("the empty text got a code",
            assertThrows(IllegalArgumentException.class, () -> new ShannonFano().codes("")).getMessage(),
            is("Кодировать пустой текст нечем"));
    }
}
