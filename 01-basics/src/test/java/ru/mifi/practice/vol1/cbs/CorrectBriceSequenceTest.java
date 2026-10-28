package ru.mifi.practice.vol1.cbs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Правильная скобочная последовательность")
final class CorrectBriceSequenceTest {

    @DisplayName("Пустая строка правильна")
    @Test
    @Timeout(1)
    void acceptsEmptyInput() {
        assertThat("empty input dont count as correct",
            new CorrectBriceSequence.Default().isCorrect(""), is(true));
    }

    @DisplayName("Вложенные скобки правильны")
    @Test
    @Timeout(1)
    void acceptsNestedBrackets() {
        assertThat("nested brackets dont count as correct",
            new CorrectBriceSequence.Default().isCorrect("({()})"), is(true));
    }

    @DisplayName("Незакрытая скобка неправильна")
    @Test
    @Timeout(1)
    void rejectsUnclosedBracket() {
        assertThat("unclosed bracket counts as correct",
            new CorrectBriceSequence.Default().isCorrect("(()"), is(false));
    }

    @DisplayName("Перепутанные типы скобок неправильны")
    @Test
    @Timeout(1)
    void rejectsCrossedBracketTypes() {
        assertThat("crossed bracket types count as correct",
            new CorrectBriceSequence.Default().isCorrect("({)}"), is(false));
    }

    @DisplayName("Закрывающая скобка без открывающей неправильна")
    @Test
    @Timeout(1)
    void rejectsClosingBracketWithoutOpening() {
        assertThat("a lone closing bracket dont count as incorrect",
            new CorrectBriceSequence.Default().isCorrect(")"), is(false));
    }

    @DisplayName("Лишняя закрывающая скобка в конце неправильна")
    @Test
    @Timeout(1)
    void rejectsTrailingClosingBracket() {
        assertThat("a trailing closing bracket dont count as incorrect",
            new CorrectBriceSequence.Default().isCorrect("())"), is(false));
    }
}
