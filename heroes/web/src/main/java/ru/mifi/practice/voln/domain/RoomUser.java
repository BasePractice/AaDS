package ru.mifi.practice.voln.domain;

import lombok.Builder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.UUID;

/** Подключённый к комнате пользователь с его SSE-каналом. */
@Builder(toBuilder = true)
public record RoomUser(UUID userId, String username, SseEmitter emitter,
                       LocalDateTime connected, LocalDateTime lastActivity) {
}
