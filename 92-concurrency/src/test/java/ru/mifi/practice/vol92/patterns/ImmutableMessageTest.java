package ru.mifi.practice.vol92.patterns;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Неизменяемое сообщение")
final class ImmutableMessageTest {

    @DisplayName("Поля сообщения читаются без изменений после конструктора")
    @Test
    @Timeout(1)
    void keepsFieldsAfterConstruction() {
        ImmutableMessage message = new ImmutableMessage("поток-1", "готово", 42L);
        assertThat("sender changes after construction",
            message.sender(), is("поток-1"));
        assertThat("body changes after construction",
            message.body(), is("готово"));
        assertThat("stamp changes after construction",
            message.stamp(), is(42L));
    }
}
