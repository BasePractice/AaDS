package ru.mifi.practice.voln.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//FIXME: Поля запроса принимаются без проверок — нужно навесить валидацию имени, пароля и профиля, как в SignUpRequest
/** Запрос на регистрацию пользователя из внешнего источника. */
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
