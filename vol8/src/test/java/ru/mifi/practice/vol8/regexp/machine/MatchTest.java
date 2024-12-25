package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Match")
class MatchTest {

    protected static Stream<Arguments> patternMatching() {
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

    @ParameterizedTest
    @MethodSource("patternMatching")
    void match(boolean isMatch, String input, String pattern) {
        Tree tree = new Tree.Default(pattern);
        Match match = new Match.Machine(tree);
        boolean matched = match.match(input);
        if (isMatch) {
            assertTrue(matched);
        } else {
            assertFalse(matched);
        }
    }
}
