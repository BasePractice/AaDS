package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка молчания: в локальной партии разговаривать не с кем. */
@DisplayName("Канал реплик")
final class TalkTest {

    @DisplayName("Локальной партии панель реплик не нужна")
    @Test
    @Timeout(1)
    void needsNoChatPanelInALocalBattle() {
        assertThat("a local battle asks for a chat panel", new Talk.Silent().present(), is(false));
    }

    @DisplayName("Попытка заговорить в локальной партии отвергается")
    @Test
    @Timeout(1)
    void refusesToSpeakInALocalBattle() {
        assertThrows(IllegalStateException.class, () -> new Talk.Silent().say("привет"),
            "a local battle sends the message into nowhere");
    }
}
