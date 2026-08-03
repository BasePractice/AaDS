package ru.mifi.practice.vol8.regexp.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Дерево регулярного выражения")
class TreeTest {
    private static Stream<String> patternText() {
        return Stream.of(
            "abc*d?|abce|ab?ei|a(bcde[cei])+|d(c|e|i)?i",
            "abc*d?|abce|ab?e?i?|a(bcde[cei])+|d[cei]?i",
            "p(abc*d?|ab?e?i?|a(bcde[cei])+|d[cei]?i)ab",
            "(a|b*(c?d)+|e)|(of|pt)",
            "(a|b*(c?d)+|e)|p[^of]+t"
        );
    }

    @DisplayName("Разбор восстанавливает исходный текст шаблона")
    @ParameterizedTest
    @Timeout(5)
    @MethodSource("patternText")
    void roundTripsThePatternText(String text) {
        assertThat("parsed tree dont round-trip the pattern text",
            new Tree.Default(text).root().toString(), is(text));
    }

    @DisplayName("Текстовый визитор воспроизводит дерево")
    @ParameterizedTest
    @Timeout(5)
    @MethodSource("patternText")
    void reproducesTheTreeThroughVisitor(String text) {
        Tree.Default tree = new Tree.Default(text);
        OriginalTextGenerator visitor = new OriginalTextGenerator();
        tree.visit(visitor);
        assertThat("original-text visitor dont reproduce the tree",
            tree.root().toString(), is(visitor.toString()));
    }
}
