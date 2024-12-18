package ru.mifi.practice.vol8.regexp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.vol8.regexp.visitor.TextVisitor;
import ru.mifi.practice.vol8.regexp.visitor.UTextVisitor;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tree")
class TreeTest {
    private static Stream<Arguments> patternText() {
        return Stream.of(
            Arguments.of("1.utext", "abc*d?|abce|ab?ei|a(bcde[cei])+|d(c|e|i)?i"),
            Arguments.of("2.utext", "abc*d?|abce|ab?e?i?|a(bcde[cei])+|d[cei]?i"),
            Arguments.of("3.utext", "p(abc*d?|ab?e?i?|a(bcde[cei])+|d[cei]?i)ab"),
            Arguments.of("4.utext", "(a|b*(c?d)+|e)|(of|pt)")
        );
    }

    @DisplayName("parse")
    @ParameterizedTest
    @MethodSource("patternText")
    void parse(String name, String text) throws IOException {
        Tree.Default tree = new Tree.Default(text);
        Tree.Node node = tree.root();
        assertEquals(text, node.toString());
        UTextVisitor uTextVisitor = new UTextVisitor();
        TextVisitor textVisitor = new TextVisitor();
        tree.visit(uTextVisitor);
        uTextVisitor.writeFile(name);
        tree.visit(textVisitor);
        assertEquals(textVisitor.toString(), node.toString());
    }
}
