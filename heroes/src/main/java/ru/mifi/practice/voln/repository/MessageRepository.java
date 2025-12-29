package ru.mifi.practice.voln.repository;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MessageRepository {
    Mono<Void> receivedMessage(UUID userId, String text);
}
