package ru.mifi.practice.voln.repository;

import reactor.core.publisher.Mono;
import ru.mifi.practice.voln.entity.UserEntity;

public interface UserRepository {
    Mono<UserEntity> findUserTelegramId(Long telegramId);

    Mono<UserEntity> registrationTelegramUser(Long telegramId, String telegramUsername,
                                              String telegramPhone, String telegramFirstName, String telegramLastName);
}
