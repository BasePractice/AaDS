package ru.mifi.practice.voln.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/** Проверка учёта присутствия пользователей чата по сессиям. */
@DisplayName("Присутствие в комнате")
final class UserPresenceServiceTest {

    @DisplayName("Пришедший пользователь числится в комнате")
    @Test
    @Timeout(1)
    void countsTheArrivedUser() {
        UserPresenceService presence = new UserPresenceService();
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        assertThat("the arrived user is missing from the room",
            presence.getUniqueUsernames(), contains("боец"));
    }

    @DisplayName("Два соединения одного имени считаются одним пользователем")
    @Test
    @Timeout(1)
    void countsTwoSessionsOfTheSameNameAsOneUser() {
        UserPresenceService presence = new UserPresenceService();
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        assertThat("the second session of the same name counts as another user",
            presence.getUniqueUsersCount(), is(1));
    }

    @DisplayName("Каждое соединение считается отдельно")
    @Test
    @Timeout(1)
    void countsEverySessionApart() {
        UserPresenceService presence = new UserPresenceService();
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        assertThat("the second connection of the same name is lost",
            presence.getTotalConnections(), is(2));
    }

    @DisplayName("Ушедшее соединение больше не числится")
    @Test
    @Timeout(1)
    void forgetsTheLeftSession() {
        UserPresenceService presence = new UserPresenceService();
        UUID session = UUID.randomUUID();
        presence.addUser("боец", session, new SseEmitter());
        presence.removeUser(session);
        assertThat("the left session is still counted", presence.getTotalConnections(), is(0));
    }

    @DisplayName("Имя освобождается, когда уходит последнее соединение")
    @Test
    @Timeout(1)
    void freesTheNameWithTheLastSession() {
        UserPresenceService presence = new UserPresenceService();
        UUID session = UUID.randomUUID();
        presence.addUser("боец", session, new SseEmitter());
        presence.removeUser(session);
        assertThat("the name stays taken after the last session left",
            presence.isUsernameTaken("боец"), is(false));
    }

    @DisplayName("Имя остаётся занятым, пока держится второе соединение")
    @Test
    @Timeout(1)
    void keepsTheNameWhileAnotherSessionHolds() {
        UserPresenceService presence = new UserPresenceService();
        UUID session = UUID.randomUUID();
        presence.addUser("боец", session, new SseEmitter());
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        presence.removeUser(session);
        assertThat("the name is freed while another session holds it",
            presence.isUsernameTaken("боец"), is(true));
    }

    @DisplayName("Сессия находится по своему номеру")
    @Test
    @Timeout(1)
    void findsTheSessionByItsId() {
        UserPresenceService presence = new UserPresenceService();
        UUID session = UUID.randomUUID();
        presence.addUser("боец", session, new SseEmitter());
        assertThat("the session is lost by its own id",
            presence.getUserBySessionId(session).username(), is("боец"));
    }

    @DisplayName("Неизвестная сессия ничего не находит")
    @Test
    @Timeout(1)
    void findsNothingForAnUnknownSession() {
        assertThat("an unknown session finds a user",
            new UserPresenceService().getUserBySessionId(UUID.randomUUID()), is(nullValue()));
    }

    @DisplayName("Отметка активности не заводит вторую сессию")
    @Test
    @Timeout(1)
    void keepsTheSessionOnActivity() {
        UserPresenceService presence = new UserPresenceService();
        UUID session = UUID.randomUUID();
        presence.addUser("боец", session, new SseEmitter());
        presence.updateUserActivity(session);
        assertThat("the activity mark duplicates the session", presence.getAllUsers(), hasSize(1));
    }

    @DisplayName("Сводка по имени считает его соединения")
    @Test
    @Timeout(1)
    void reportsTheConnectionsOfTheName() {
        UserPresenceService presence = new UserPresenceService();
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        presence.addUser("боец", UUID.randomUUID(), new SseEmitter());
        assertThat("the summary miscounts the connections of the name",
            presence.getUsersWithInfo().get(0).get("connections"), is(2));
    }
}
