package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Совпадение с неполным входом")
final class PrefixMatchTest {

    @DisplayName("Шаблон целиком совпадает с полным входом")
    @Test
    @Timeout(5)
    void matchesTheWholeInput() {
        assertThat("a complete input dont match", matches("ab", "ab"), is(true));
    }

    @DisplayName("Шаблон не совпадает с оборванным входом")
    @Test
    @Timeout(5)
    void cannotMatchATruncatedInput() {
        assertThat("a truncated input matches because the machine reports success "
            + "even when the continuation was not accepted", matches("ab", "a"), is(false));
    }

    @DisplayName("Длинный шаблон не совпадает с одним символом")
    @Test
    @Timeout(5)
    void cannotMatchASingleCharacterAgainstALongPattern() {
        assertThat("a single character matches a three symbol pattern", matches("abc", "a"), is(false));
    }

    @DisplayName("Шаблон не совпадает с лишним хвостом")
    @Test
    @Timeout(5)
    void cannotMatchAnInputWithATail() {
        assertThat("an input with a trailing tail matches", matches("ab", "abc"), is(false));
    }

    private static boolean matches(String pattern, String input) {
        return new Matcher.Default(new Tree.Default(pattern)).match(input);
    }
}
