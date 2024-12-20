package ru.mifi.practice.vol8.regexp;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public abstract class AbstractPatternTest {
    protected static Stream<Arguments> patternText() {
        return Stream.of(
            Arguments.of("1", "abc*d?|abce|ab?ei|a(bcde[cei])+|d(c|e|i)?i"),
            Arguments.of("2", "abc*d?|abce|ab?e?i?|a(bcde[cei])+|d[cei]?i"),
            Arguments.of("3", "p(abc*d?|ab?e?i?|a(bcde[cei])+|d[cei]?i)ab"),
            Arguments.of("4", "(a|b*(c?d)+|e)|(of|pt)")
        );
    }
}
