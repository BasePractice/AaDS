package ru.mifi.practice.voln.repository.implementation;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mifi.practice.voln.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageRepositoryPostgres implements MessageRepository {
    private final Map<UUID, List<String>> telegramUserMessages = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> receivedMessage(UUID userId, String text) {
        telegramUserMessages.computeIfAbsent(userId, (k) -> new ArrayList<>()).add(text);
        return Mono.empty();
    }
}
