package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Сопоставление строки с шаблоном")
class MatcherTest {
    private static Stream<Arguments> patternMatching() {
        return Stream.of(
            Arguments.of(true, "a", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(true, "e", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(true, "of", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(true, "pt", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(true, "bddd", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(true, "cdcd", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(true, "bcddcd", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(true, "bbbbbd", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(false, "hello", "(a|b*(c?d)+|e)|(of|pt)"),
            Arguments.of(false, "bbcdddo", "(a|b*(c?d)+|e)|(of|pt)")
        );
    }

    @DisplayName("Автомат относит строку к языку шаблона правильно")
    @ParameterizedTest
    @Timeout(5)
    @MethodSource("patternMatching")
    void classifiesInputAgainstPattern(boolean isMatch, String input, String pattern) {
        Tree tree = new Tree.Default(pattern);
        Matcher match = new Matcher.Default(tree);
        assertThat("automaton dont classify the input against the pattern correctly",
            match.match(input), is(isMatch));
    }
}
