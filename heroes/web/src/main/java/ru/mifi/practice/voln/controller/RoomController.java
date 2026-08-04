package ru.mifi.practice.voln.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.mifi.practice.voln.service.RoomService;

import java.util.UUID;

/**
 * REST-контроллер комнат сетевого боя.
 *
 * <p>Отвечает и принимает обычный текст того же протокола, что ходит по потоку событий: партию
 * видно в curl целиком, а разбирать её на клиенте можно без библиотеки разбора JSON.
 */
@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Комнаты", description = "Подбор соперника и обмен ходами")
public class RoomController {
    private final RoomService rooms;

    public RoomController(RoomService rooms) {
        this.rooms = rooms;
    }

    @Operation(summary = "Занять свободный слот или создать комнату и ждать соперника")
    @PostMapping(path = "/join", produces = MediaType.TEXT_PLAIN_VALUE)
    public String join(@RequestParam("nickname") String nickname) {
        return rooms.join(nickname).encode();
    }

    @Operation(summary = "Поток событий комнаты: начало боя, ходы соперника и его реплики")
    @GetMapping(path = "/{room-id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable("room-id") UUID id, @RequestParam("left") boolean left) {
        return rooms.stream(id, left);
    }

    @Operation(summary = "Передать сопернику ход или реплику")
    @PostMapping(path = "/{room-id}/message", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> message(@PathVariable("room-id") UUID id,
                                        @RequestParam("left") boolean left,
                                        @RequestBody String message) {
        rooms.relay(id, left, message.trim());
        return ResponseEntity.accepted().build();
    }
}
