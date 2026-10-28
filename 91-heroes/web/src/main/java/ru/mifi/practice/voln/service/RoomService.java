package ru.mifi.practice.voln.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.mifi.practice.voln.domain.Channel;
import ru.mifi.practice.voln.domain.Room;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Подбор соперника и передача ходов между сторонами комнаты.
 *
 * <p>Правило подбора простейшее: занять первую комнату со свободным слотом, а если такой нет —
 * завести новую и ждать. Это очередь ожидающих длиной в одну комнату, чего для боя двоих
 * достаточно; турнирная сетка и рейтинг сюда не входят.
 */
@Service
public class RoomService {
    private static final long TIMEOUT = 60L * 60 * 1000;

    private final Map<UUID, Room> rooms = new ConcurrentHashMap<>();

    /** Занять свободный слот или создать комнату и ждать соперника. */
    public synchronized Seat join(String nickname) {
        Room room = rooms.values().stream().filter(Room::vacant).findFirst().orElseGet(this::opened);
        return new Seat(room.id(), room.take(nickname), room.seed());
    }

    /** Подписать сторону на поток событий комнаты. */
    public SseEmitter stream(UUID id, boolean left) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitter.onCompletion(() -> leave(id, left));
        emitter.onTimeout(() -> leave(id, left));
        emitter.onError(error -> leave(id, left));
        attach(id, left, data -> emit(emitter, data));
        return emitter;
    }

    /** Привязать канал к стороне; когда встали обе, обеим уходит начало боя. */
    public void attach(UUID id, boolean left, Channel channel) {
        final Room room = room(id);
        room.attach(left, channel);
        if (room.ready()) {
            room.broadcast("START;" + room.seed());
        }
    }

    /** Передать строку сопернику той стороны, что её прислала. */
    public void relay(UUID id, boolean left, String message) {
        room(id).send(!left, message);
    }

    /** Отцепить сторону: соперник узнаёт об уходе, опустевшая комната исчезает. */
    public void leave(UUID id, boolean left) {
        Room room = rooms.get(id);
        if (room == null) {
            return;
        }
        String nickname = room.player(left);
        if (room.detach(left)) {
            rooms.remove(id);
        } else {
            room.send(!left, "LEAVE;" + nickname);
        }
    }

    private void emit(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().data(data));
        } catch (IOException | IllegalStateException e) {
            emitter.completeWithError(e);
        }
    }

    private Room room(UUID id) {
        Room room = rooms.get(id);
        if (room == null) {
            throw new IllegalArgumentException("Комната не найдена: " + id);
        }
        return room;
    }

    private Room opened() {
        Room room = new Room();
        rooms.put(room.id(), room);
        return room;
    }

    /** Место в комнате: сама комната, доставшаяся сторона и зерно расстановки. */
    public record Seat(UUID room, boolean left, long seed) {

        /** Ответ клиенту в том же текстовом протоколе, что и события комнаты. */
        public String encode() {
            return String.join(";", "ROOM", room.toString(), String.valueOf(left), String.valueOf(seed));
        }
    }
}
