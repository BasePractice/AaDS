package ru.mifi.practice.voln.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка подбора соперника и передачи ходов между сторонами комнаты. */
@DisplayName("Комнаты сетевого боя")
final class RoomServiceTest {

    @DisplayName("Первому игроку достаётся левая сторона")
    @Test
    @Timeout(1)
    void seatsTheFirstPlayerOnTheLeft() {
        assertThat("the first player dont get the left side",
            new RoomService().join("первый").left(), is(true));
    }

    @DisplayName("Второй игрок попадает в ту же комнату")
    @Test
    @Timeout(1)
    void seatsTheSecondPlayerInTheSameRoom() {
        RoomService rooms = new RoomService();
        UUID first = rooms.join("первый").room();
        assertThat("the second player lands in another room", rooms.join("второй").room(), is(first));
    }

    @DisplayName("Второму игроку достаётся правая сторона")
    @Test
    @Timeout(1)
    void seatsTheSecondPlayerOnTheRight() {
        RoomService rooms = new RoomService();
        rooms.join("первый");
        assertThat("the second player dont get the right side", rooms.join("второй").left(), is(false));
    }

    @DisplayName("Третий игрок заводит новую комнату")
    @Test
    @Timeout(1)
    void opensANewRoomForTheThirdPlayer() {
        RoomService rooms = new RoomService();
        UUID first = rooms.join("первый").room();
        rooms.join("второй");
        assertThat("the third player squeezes into a full room", rooms.join("третий").room(), is(not(first)));
    }

    @DisplayName("Обе стороны получают одно зерно расстановки")
    @Test
    @Timeout(1)
    void sharesTheSameSeedBetweenTheSides() {
        RoomService rooms = new RoomService();
        long seed = rooms.join("первый").seed();
        assertThat("the sides get different seeds", rooms.join("второй").seed(), is(seed));
    }

    @DisplayName("Когда встали обе стороны, приходит начало боя")
    @Test
    @Timeout(1)
    void announcesTheStartWhenBothSidesListen() {
        RoomService rooms = new RoomService();
        List<String> heard = new ArrayList<>();
        RoomService.Seat first = rooms.join("первый");
        rooms.join("второй");
        rooms.attach(first.room(), true, heard::add);
        rooms.attach(first.room(), false, line -> { });
        assertThat("the start dont reach the side that came first",
            heard, hasItem("START;" + first.seed()));
    }

    @DisplayName("Пока сторона одна, начала боя не приходит")
    @Test
    @Timeout(1)
    void keepsSilentUntilTheOpponentArrives() {
        RoomService rooms = new RoomService();
        List<String> heard = new ArrayList<>();
        rooms.attach(rooms.join("первый").room(), true, heard::add);
        assertThat("the start arrives before the opponent", heard, is(empty()));
    }

    @DisplayName("Ход уходит сопернику")
    @Test
    @Timeout(1)
    void relaysTheActionToTheOpponent() {
        RoomService rooms = new RoomService();
        List<String> heard = new ArrayList<>();
        UUID room = rooms.join("первый").room();
        rooms.attach(room, false, heard::add);
        rooms.relay(room, true, "ACTION;ATTACK;3;7");
        assertThat("the action dont reach the opponent", heard, hasItem("ACTION;ATTACK;3;7"));
    }

    @DisplayName("Ход не возвращается отправителю")
    @Test
    @Timeout(1)
    void keepsTheActionAwayFromItsSender() {
        RoomService rooms = new RoomService();
        List<String> heard = new ArrayList<>();
        UUID room = rooms.join("первый").room();
        rooms.attach(room, true, heard::add);
        rooms.relay(room, true, "ACTION;ATTACK;3;7");
        assertThat("the action comes back to its sender", heard, not(hasItem("ACTION;ATTACK;3;7")));
    }

    @DisplayName("Об уходе стороны соперник узнаёт")
    @Test
    @Timeout(1)
    void tellsTheOpponentAboutTheLeave() {
        RoomService rooms = new RoomService();
        List<String> heard = new ArrayList<>();
        UUID room = rooms.join("первый").room();
        rooms.join("второй");
        rooms.attach(room, true, line -> { });
        rooms.attach(room, false, heard::add);
        rooms.leave(room, true);
        assertThat("the opponent dont learn about the leave", heard, hasItem("LEAVE;первый"));
    }

    @DisplayName("Незнакомая комната отвергается сразу")
    @Test
    @Timeout(1)
    void refusesAnUnknownRoom() {
        assertThrows(IllegalArgumentException.class,
            () -> new RoomService().relay(UUID.randomUUID(), true, "ACTION;MOVE;1;1"),
            "an unknown room passes the check");
    }

    @DisplayName("Ответ о месте пишется протоколом комнаты")
    @Test
    @Timeout(1)
    void writesTheSeatInTheRoomProtocol() {
        UUID room = UUID.fromString("00000000-0000-0000-0000-000000000001");
        assertThat("the seat answer dont follow the room protocol",
            new RoomService.Seat(room, true, 42L).encode(),
            is("ROOM;00000000-0000-0000-0000-000000000001;true;42"));
    }
}
