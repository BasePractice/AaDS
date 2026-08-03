package ru.mifi.practice.voln.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Модель пользователя с профилем и ролями. */
@Builder
@Getter
@Setter
public final class UserModel {
    private UUID id;
    private String username;
    private String password;
    private String nickname;
    private String lastName;
    private String firstName;
    private String middleName;
    @Builder.Default
    private List<String> authorities = new ArrayList<>();

    public void addAuthority(String authorityName) {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        authorities.add("ROLE_" + authorityName);
    }

    @Override
    public String toString() {
        return username;
    }
}
