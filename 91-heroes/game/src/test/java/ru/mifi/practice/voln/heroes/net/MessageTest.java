package ru.mifi.practice.voln.heroes.net;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.heroes.Tactics;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка текстового протокола комнаты: запись, разбор и отказ на неизвестном типе. */
@DisplayName("Сообщение комнаты")
final class MessageTest {

    @DisplayName("Ход переживает запись и разбор")
    @Test
    @Timeout(1)
    void survivesTheRoundTripOfAnAction() {
        Message.Action action = new Message.Action(Tactics.Kind.ATTACK, 3, 7);
        assertThat("the action dont survive encoding and decoding",
            Message.decode(action.encode()), is(action));
    }

    @DisplayName("Начало боя переживает запись и разбор")
    @Test
    @Timeout(1)
    void survivesTheRoundTripOfTheStart() {
        Message.Start start = new Message.Start(-42L);
        assertThat("the start dont survive encoding and decoding",
            Message.decode(start.encode()), is(start));
    }

    @DisplayName("Реплика с точкой с запятой не рвётся при разборе")
    @Test
    @Timeout(1)
    void keepsTheSeparatorInsideTheChatText() {
        Message.Chat chat = new Message.Chat("герой", "раз; два; три");
        assertThat("the separator inside the text breaks the chat message",
            Message.decode(chat.encode()), is(chat));
    }

    @DisplayName("Уход соперника переживает запись и разбор")
    @Test
    @Timeout(1)
    void survivesTheRoundTripOfTheLeave() {
        Message.Leave leave = new Message.Leave("герой");
        assertThat("the leave dont survive encoding and decoding",
            Message.decode(leave.encode()), is(leave));
    }

    @DisplayName("Неизвестный тип отвергается сразу")
    @Test
    @Timeout(1)
    void refusesAnUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> Message.decode("SURRENDER;герой"),
            "an unknown message type passes the parser");
    }
}
