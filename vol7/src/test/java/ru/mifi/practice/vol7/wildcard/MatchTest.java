package ru.mifi.practice.vol7.wildcard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mifi.practice.vol7.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Сопоставление с шаблоном")
final class MatchTest {

    @DisplayName("Звёздочка покрывает произвольный хвост")
    @Test
    void letsAsteriskCoverAnyTail() {
        assertThat("asterisk dont cover the tail",
            new Match.DefaultMatch().isMatch("no*", "none", Counter.create()), is(true));
    }

    @DisplayName("Вопрос покрывает ровно один символ")
    @Test
    void letsQuestionCoverExactlyOneCharacter() {
        assertThat("question dont cover exactly one character",
            new Match.DefaultMatch().isMatch("non?", "none", Counter.create()), is(true));
    }

    @DisplayName("Несовпадающий шаблон отвергается")
    @Test
    void rejectsANonMatchingPattern() {
        assertThat("a non matching pattern is accepted",
            new Match.DefaultMatch().isMatch("no?", "none", Counter.create()), is(false));
    }

    @DisplayName("Аббревиатура принимается в том же порядке аргументов, что и шаблон")
    @Test
    void takesAbbreviationAsThePatternArgument() {
        assertThat("abbreviation matching swaps the interface arguments",
            new Match.AbbreviationMatch().isMatch("ADS", "Algorithm and Data Structure", Counter.create()),
            is(true));
    }

    @DisplayName("Чужая аббревиатура отвергается")
    @Test
    void rejectsAForeignAbbreviation() {
        assertThat("a foreign abbreviation is accepted",
            new Match.AbbreviationMatch().isMatch("XYZ", "Algorithm and Data Structure", Counter.create()),
            is(false));
    }
}
