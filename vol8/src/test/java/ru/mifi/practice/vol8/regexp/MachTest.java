package ru.mifi.practice.vol8.regexp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.mifi.practice.vol8.regexp.Mach.Compiler.compile;

@DisplayName("Mach")
class MachTest extends AbstractPatternTest {
    @DisplayName("compile")
    @ParameterizedTest
    @MethodSource("patternText")
    void parse(String name, String text) throws IOException {
        Mach mach = compile(text);
        assertTrue(mach.match("blabla"));
    }
}
