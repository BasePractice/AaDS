package ru.mifi.practice.voln.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mifi.practice.voln.domain.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByTelegramId(Long telegramId);

    Optional<UserEntity> findByUsername(String username);
}
