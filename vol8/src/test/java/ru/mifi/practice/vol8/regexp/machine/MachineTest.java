package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Построение автомата по дереву")
class MachineTest {
    private static Stream<String> patternText() {
        return Stream.of(
            "abc*d?|abce|ab?ei|a(bcde[cei])+|d(c|e|i)?i",
            "abc*d?|abce|ab?e?i?|a(bcde[cei])+|d[cei]?i",
            "p(abc*d?|ab?e?i?|a(bcde[cei])+|d[cei]?i)ab",
            "(a|b*(c?d)+|e)|(of|pt)",
            "(a|b*(c?d)+|e)|p[^of]+t"
        );
    }

    @DisplayName("Генератор строит состояние автомата для каждого шаблона")
    @ParameterizedTest
    @Timeout(5)
    @MethodSource("patternText")
    void buildsStateForEveryPattern(String text) {
        MachineGenerator generator = new MachineGenerator();
        Tree.Default tree = new Tree.Default(text);
        tree.visit(generator);
        assertThat("machine generator dont build a state for the pattern",
            generator.getState(), notNullValue());
    }
}
