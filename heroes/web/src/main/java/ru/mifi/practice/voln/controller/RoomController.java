package ru.mifi.practice.voln.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mifi.practice.voln.domain.model.JoinResult;
import ru.mifi.practice.voln.domain.model.PingResult;

import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Комнаты", description = "Комнаты комнаты")
public class RoomController {

    @Operation(summary = "Доступен только авторизованным пользователям")
    @GetMapping(path = "/{room-id}/join", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Stream<String>> stream(@RequestHeader("X-User-ID") Long userId, @PathVariable("room-id") UUID id) {
        return ResponseEntity.ok(Stream.of());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(path = "/{room-id}/ping")
    public ResponseEntity<PingResult> ping(@RequestHeader("X-User-ID") Long userId, @PathVariable("room-id") UUID id) {
        return ResponseEntity.ok(PingResult.builder().build());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(path = "/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JoinResult> join(@RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(JoinResult.builder().build());
    }
}
