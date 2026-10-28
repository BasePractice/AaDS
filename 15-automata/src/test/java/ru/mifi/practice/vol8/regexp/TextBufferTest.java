package ru.mifi.practice.vol8.regexp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка накопителя текста, которым генераторы диаграмм собирают свой вывод. */
@DisplayName("Накопитель текста")
final class TextBufferTest {

    @DisplayName("Новый накопитель пуст")
    @Test
    @Timeout(1)
    void startsEmpty() {
        assertThat("a fresh buffer is not empty", new TextBuffer().toString(), is(""));
    }

    @DisplayName("Дописанные куски идут подряд")
    @Test
    @Timeout(1)
    void keepsTheAppendedPartsInARow() {
        TextBuffer buffer = new TextBuffer();
        buffer.append("состояние");
        buffer.append(1);
        assertThat("the appended parts dont go in a row", buffer.toString(), is("состояние1"));
    }

    @DisplayName("Строка завершается переводом строки")
    @Test
    @Timeout(1)
    void closesTheLineWithABreak() {
        TextBuffer buffer = new TextBuffer();
        buffer.line("состояние");
        assertThat("the line dont end with a break", buffer.toString(), is("состояние\n"));
    }

    @DisplayName("Сброс опустошает накопитель")
    @Test
    @Timeout(1)
    void emptiesOnReset() {
        TextBuffer buffer = new TextBuffer();
        buffer.line("состояние");
        buffer.reset();
        assertThat("resetting dont empty the buffer", buffer.toString(), is(""));
    }
}
