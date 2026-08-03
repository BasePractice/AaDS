package ru.mifi.practice.voln.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.mifi.practice.voln.domain.RoomUser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserPresenceService {
    private static final long INACTIVITY_TIMEOUT = 5 * 60 * 1000;
    private final Map<UUID, RoomUser> activeUsers = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> usernameToSessions = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 60000, scheduler = "schedulingService")
    public void cleanupInactiveUsers() {
        List<UUID> toRemove = new ArrayList<>();

        for (RoomUser user : activeUsers.values()) {
            long inactiveTime = Duration.between(user.lastActivity(), LocalDateTime.now()).toMillis();
            if (inactiveTime > INACTIVITY_TIMEOUT) {
                toRemove.add(user.userId());
            }
        }

        for (UUID userId : toRemove) {
            removeUser(userId);
        }
    }

    public RoomUser addUser(String username, UUID userId, SseEmitter emitter) {
        RoomUser user = new RoomUser(userId, username, emitter, LocalDateTime.now(), LocalDateTime.now());
        activeUsers.put(userId, user);
        usernameToSessions.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet()).add(userId);
        return user;
    }

    public void removeUser(UUID userId) {
        RoomUser user = activeUsers.remove(userId);
        if (user != null) {
            String username = user.username();
            Set<UUID> sessions = usernameToSessions.get(username);
            if (sessions != null) {
                sessions.remove(userId);
                if (sessions.isEmpty()) {
                    usernameToSessions.remove(username);
                }
            }
        }
    }

    public void updateUserActivity(UUID sessionId) {
        RoomUser user = activeUsers.get(sessionId);
        if (user != null) {
            activeUsers.put(sessionId, user.toBuilder().lastActivity(LocalDateTime.now()).build());
        }
    }

    public List<String> getUniqueUsernames() {
        return new ArrayList<>(usernameToSessions.keySet());
    }

    public List<RoomUser> getAllUsers() {
        return new ArrayList<>(activeUsers.values());
    }

    public List<RoomUser> getUsersByUsername(String username) {
        Set<UUID> sessions = usernameToSessions.get(username);
        if (sessions == null) {
            return new ArrayList<>();
        }

        List<RoomUser> users = new ArrayList<>();
        for (UUID userId : sessions) {
            RoomUser user = activeUsers.get(userId);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    public boolean isUsernameTaken(String username) {
        return usernameToSessions.containsKey(username);
    }

    public int getTotalConnections() {
        return activeUsers.size();
    }

    public int getUniqueUsersCount() {
        return usernameToSessions.size();
    }

    public RoomUser getUserBySessionId(UUID sessionId) {
        return activeUsers.get(sessionId);
    }

    public List<Map<String, Object>> getUsersWithInfo() {
        List<Map<String, Object>> usersInfo = new ArrayList<>();

        for (Map.Entry<String, Set<UUID>> entry : usernameToSessions.entrySet()) {
            String username = entry.getKey();
            Set<UUID> sessions = entry.getValue();
            List<RoomUser> users = getUsersByUsername(username);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", username);
            userInfo.put("connections", sessions.size());
            userInfo.put("firstConnection", users.stream()
                .map(RoomUser::connected)
                .min(LocalDateTime::compareTo)
                .orElse(null));
            userInfo.put("lastActivity", users.stream()
                .map(RoomUser::lastActivity)
                .max(LocalDateTime::compareTo)
                .orElse(null));
            userInfo.put("sessions", sessions.size());

            usersInfo.add(userInfo);
        }

        return usersInfo;
    }
}
