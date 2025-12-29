package ru.mifi.practice.voln.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.mifi.practice.voln.domain.RoomUser;
import ru.mifi.practice.voln.service.UserPresenceService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class ChatController {

    private static final int MAX_HISTORY = 100;
    private final UserPresenceService userPresenceService;
    private final List<RoomMessage> messageHistory = new ArrayList<>();

    public ChatController(UserPresenceService userPresenceService) {
        this.userPresenceService = userPresenceService;
    }

    @GetMapping("/api/chat/connect")
    public SseEmitter connect(@RequestParam String username,
                              HttpServletRequest request) {

        // Генерируем уникальный ID сессии
        UUID sessionId = UUID.randomUUID();

        // Проверяем, не занято ли имя пользователя (опционально)
        if (userPresenceService.isUsernameTaken(username)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Имя пользователя уже занято"
            );
        }

        SseEmitter emitter = new SseEmitter(60L * 1000 * 60); // 60 минут timeout

        // Создаем пользователя
        RoomUser user = userPresenceService.addUser(username, sessionId, emitter);

        // Отправляем информацию о пользователе
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sessionId", sessionId);
        userInfo.put("username", username);
        userInfo.put("connectedAt", user.connected());

        try {
            // Отправляем данные о соединении
            emitter.send(SseEmitter.event()
                .name("connected")
                .data(userInfo));

            // Отправляем историю сообщений
            for (RoomMessage message : messageHistory) {
                emitter.send(SseEmitter.event()
                    .name("message")
                    .data(message));
            }

            // Отправляем текущий список пользователей
            sendUserListUpdate();

        } catch (IOException e) {
            userPresenceService.removeUser(sessionId);
            emitter.completeWithError(e);
        }

        // Обработчики событий
        emitter.onCompletion(() -> {
            handleUserDisconnect(sessionId, username, "disconnected");
        });

        emitter.onTimeout(() -> {
            handleUserDisconnect(sessionId, username, "timeout");
        });

        emitter.onError((e) -> {
            handleUserDisconnect(sessionId, username, "error");
        });

        return emitter;
    }

    @PostMapping("/api/chat/send")
    public ResponseEntity<?> sendMessage(@RequestBody RoomMessage message,
                                         @RequestHeader(value = "X-Session-Id", required = false) UUID sessionId) {

        message = message.toBuilder().timestamp(LocalDateTime.now()).build();

        // Обновляем активность пользователя
        if (sessionId != null) {
            userPresenceService.updateUserActivity(sessionId);
        }

        // Сохраняем в историю
        messageHistory.add(message);
        if (messageHistory.size() > MAX_HISTORY) {
            messageHistory.remove(0);
        }

        // Рассылаем всем подключенным клиентам
        broadcastMessage(message);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chat/users")
    public Map<String, Object> getActiveUsers() {
        Map<String, Object> response = new HashMap<>();

        response.put("uniqueUsers", userPresenceService.getUniqueUsernames());
        response.put("totalConnections", userPresenceService.getTotalConnections());
        response.put("uniqueUsersCount", userPresenceService.getUniqueUsersCount());
        response.put("usersWithInfo", userPresenceService.getUsersWithInfo());

        return response;
    }

    @GetMapping("/api/chat/users/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        List<RoomUser> users = userPresenceService.getUsersByUsername(username);

        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("activeConnections", users.size());
        userInfo.put("sessions", users.stream()
            .map(RoomUser::userId)
            .collect(Collectors.toList()));
        userInfo.put("connectedAt", users.stream()
            .map(RoomUser::connected)
            .min(LocalDateTime::compareTo)
            .orElse(null));
        userInfo.put("lastActivity", users.stream()
            .map(RoomUser::lastActivity)
            .max(LocalDateTime::compareTo)
            .orElse(null));

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/api/chat/ping")
    public ResponseEntity<?> ping(@RequestHeader("X-Session-Id") UUID sessionId) {
        userPresenceService.updateUserActivity(sessionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/chat/disconnect")
    public ResponseEntity<?> disconnect(@RequestHeader("X-Session-Id") UUID sessionId) {
        RoomUser user = userPresenceService.getUserBySessionId(sessionId);
        if (user != null) {
            handleUserDisconnect(sessionId, user.username(), "manual");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chat/stats")
    public Map<String, Object> getChatStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalMessages", messageHistory.size());
        stats.put("uniqueUsers", userPresenceService.getUniqueUsersCount());
        stats.put("totalConnections", userPresenceService.getTotalConnections());
        stats.put("activeSince", messageHistory.isEmpty() ? null : messageHistory.get(0).getTimestamp());

        return stats;
    }

    private void handleUserDisconnect(UUID sessionId, String username, String reason) {
        userPresenceService.removeUser(sessionId);

        // Отправляем уведомление об отключении
        RoomEvent disconnectEvent = new RoomEvent(
            "user_disconnected",
            String.format("%s отключился (%s)", username, reason),
            LocalDateTime.now(),
            Map.of("username", username, "reason", reason)
        );

        broadcastEvent(disconnectEvent);

        // Обновляем список пользователей
        sendUserListUpdate();
    }

    private void broadcastMessage(RoomMessage message) {
        List<RoomUser> allUsers = userPresenceService.getAllUsers();
        List<RoomUser> deadEmitters = new ArrayList<>();

        for (RoomUser user : allUsers) {
            try {
                user.emitter().send(SseEmitter.event()
                    .name("message")
                    .data(message));
            } catch (IOException e) {
                deadEmitters.add(user);
            }
        }

        // Удаляем мертвые соединения
        for (RoomUser deadUser : deadEmitters) {
            userPresenceService.removeUser(deadUser.userId());
        }
    }

    private void broadcastEvent(RoomEvent event) {
        List<RoomUser> allUsers = userPresenceService.getAllUsers();
        List<RoomUser> deadEmitters = new ArrayList<>();

        for (RoomUser user : allUsers) {
            try {
                user.emitter().send(SseEmitter.event()
                    .name(event.getUsername())
                    .data(event));
            } catch (IOException e) {
                deadEmitters.add(user);
            }
        }

        for (RoomUser deadUser : deadEmitters) {
            userPresenceService.removeUser(deadUser.userId());
        }
    }

    private void sendUserListUpdate() {
        List<String> onlineUsers = userPresenceService.getUniqueUsernames();

        Map<String, Object> userListEvent = new HashMap<>();
        userListEvent.put("type", "user_list_update");
        userListEvent.put("users", onlineUsers);
        userListEvent.put("timestamp", new Date());
        userListEvent.put("totalConnections", userPresenceService.getTotalConnections());
        userListEvent.put("uniqueUsers", userPresenceService.getUniqueUsersCount());

        broadcastEvent(new RoomEvent("user_list", "Обновление списка пользователей",
            LocalDateTime.now(), userListEvent));
    }

    @Builder(toBuilder = true)
    @Getter
    public static final class RoomMessage {
        private final String username;
        private final String text;
        private final LocalDateTime timestamp;
    }

    @Builder(toBuilder = true)
    @Getter
    public static final class RoomEvent {
        private final String username;
        private final String message;
        private final LocalDateTime timestamp;
        private final Map<String, Object> data;
    }
}
