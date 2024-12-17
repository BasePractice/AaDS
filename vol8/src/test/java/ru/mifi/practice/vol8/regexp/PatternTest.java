package ru.mifi.practice.vol8.regexp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pattern")
class PatternTest {

    private static Stream<Arguments> patternCompile() {
        return Stream.of(
            Arguments.of("abc*d?|abce|ab?ei|a(bcde[cei])+|d(c|e|i)?i"),
            Arguments.of("abc*d?|abce|ab?e?i?|a(bcde[cei])+|d[cei]?i"),
            Arguments.of("p(abc*d?|ab?e?i?|a(bcde[cei])+|d[cei]?i)ab")
        );
    }

    @DisplayName("compile")
    @ParameterizedTest
    @MethodSource("patternCompile")
    void compile(String text) {
        Pattern pattern = Pattern.compile(text);
        assertEquals(text, pattern.toString());
    }
}
