package ru.mifi.practice.voln.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class UserEntity {
    private UUID id;

    private Long telegramId;
    private String telegramUsername;
    private String telegramPhone;
    private String telegramFirstName;
    private String telegramLastName;

    public boolean isTelegramRegistered() {
        return StringUtils.hasText(telegramPhone);
    }
}
