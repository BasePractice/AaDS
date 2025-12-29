package ru.mifi.practice.voln.repository.implementation;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mifi.practice.voln.entity.UserEntity;
import ru.mifi.practice.voln.repository.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRepositoryPostgres implements UserRepository {
    private final Map<Long, UserEntity> telegramUsers = new ConcurrentHashMap<>();

    @Override
    public Mono<UserEntity> findUserTelegramId(Long telegramId) {
        return Mono.justOrEmpty(telegramUsers.get(telegramId));
    }

    @Override
    public Mono<UserEntity> registrationTelegramUser(Long telegramId,
                                                     String telegramUsername, String telegramPhone,
                                                     String telegramFirstName, String telegramLastName) {
        var user = UserEntity.builder()
            .id(UUID.randomUUID())
            .telegramId(telegramId)
            .telegramUsername(telegramUsername)
            .telegramPhone(telegramPhone)
            .telegramFirstName(telegramFirstName)
            .telegramLastName(telegramLastName)
            .build();
        telegramUsers.put(telegramId, user);
        return Mono.just(user);
    }
}
