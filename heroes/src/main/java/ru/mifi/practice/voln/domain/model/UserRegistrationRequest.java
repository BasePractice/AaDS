package ru.mifi.practice.voln.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//FIXME: Как провалидировать?
@Builder
@Setter
@Getter
public final class UserRegistrationRequest {
    private String username;
    private String password;
    private String nickname;
    private String lastName;
    private String firstName;
    private String middleName;
}
