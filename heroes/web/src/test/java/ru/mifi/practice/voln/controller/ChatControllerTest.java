package ru.mifi.practice.voln.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.mifi.practice.voln.service.UserPresenceService;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка чата: история сообщений, сводка и отказ занятому имени. */
@DisplayName("Чат комнаты")
final class ChatControllerTest {

    @DisplayName("Отправленное сообщение попадает в историю")
    @Test
    @Timeout(1)
    void keepsTheSentMessageInHistory() {
        ChatController chat = new ChatController(new UserPresenceService());
        chat.sendMessage(message("боец", "привет"), null);
        assertThat("the sent message is missing from the history",
            chat.getChatStats(), hasEntry("totalMessages", 1));
    }

    @DisplayName("Сообщению проставляется время отправки")
    @Test
    @Timeout(1)
    void stampsTheMessageWithItsTime() {
        ChatController chat = new ChatController(new UserPresenceService());
        chat.sendMessage(message("боец", "привет"), null);
        assertThat("the message goes into the history without a timestamp",
            chat.getChatStats().get("activeSince"), is(org.hamcrest.Matchers.notNullValue()));
    }

    @DisplayName("Сводка считает уникальных пользователей комнаты")
    @Test
    @Timeout(1)
    void countsTheUniqueUsersOfTheRoom() {
        UserPresenceService presence = new UserPresenceService();
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        assertThat("the summary miscounts the unique users",
            new ChatController(presence).getActiveUsers(), hasEntry("uniqueUsersCount", 1));
    }

    @DisplayName("Занятое имя не пускают в комнату")
    @Test
    @Timeout(1)
    void refusesTheTakenName() {
        UserPresenceService presence = new UserPresenceService();
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        assertThat("the taken name is let into the room",
            assertThrows(ResponseStatusException.class,
                () -> new ChatController(presence).connect("боец", null)).getStatusCode(),
            is(HttpStatus.CONFLICT));
    }

    @DisplayName("Сведений о незнакомом имени нет")
    @Test
    @Timeout(1)
    void findsNothingAboutAnUnknownName() {
        assertThat("an unknown name gets an answer with a body",
            new ChatController(new UserPresenceService()).getUserInfo("никто").getStatusCode(),
            is(HttpStatus.NOT_FOUND));
    }

    @DisplayName("Отключение убирает сессию из комнаты")
    @Test
    @Timeout(1)
    void dropsTheSessionOnDisconnect() {
        UserPresenceService presence = new UserPresenceService();
        UUID session = UUID.randomUUID();
        presence.addUser("боец", session, new SseEmitter());
        new ChatController(presence).disconnect(session);
        assertThat("the disconnected session stays in the room", presence.getTotalConnections(), is(0));
    }

    private static ChatController.RoomMessage message(String username, String text) {
        return ChatController.RoomMessage.builder().username(username).text(text).build();
    }
}
