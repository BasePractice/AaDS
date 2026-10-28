package ru.mifi.practice.voln.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.domain.entity.UserEntity;
import ru.mifi.practice.voln.domain.model.UserModel;
import ru.mifi.practice.voln.domain.model.UserRegistrationRequest;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/** Проверка переноса данных пользователя между заявкой на регистрацию, моделью и сущностью. */
@DisplayName("Перенос данных пользователя")
final class UserMapperTest {

    @DisplayName("Заявка на регистрацию доносит имя до модели")
    @Test
    @Timeout(5)
    void carriesTheNameFromTheRegistrationRequest() {
        assertThat("the name is lost between the request and the model",
            UserMapper.MAPPER.userAuthorityToUserModel(
                UserRegistrationRequest.builder().username("боец").password("пароль").build()).getUsername(),
            is("боец"));
    }

    @DisplayName("Модель доносит имя до сущности")
    @Test
    @Timeout(5)
    void carriesTheNameFromTheModel() {
        UserModel model = UserModel.builder().id(UUID.randomUUID()).username("боец").password("пароль").build();
        assertThat("the name is lost between the model and the entity",
            UserMapper.MAPPER.userModelToUserEntity(model).getUsername(), is("боец"));
    }

    @DisplayName("Сущность доносит роли до модели")
    @Test
    @Timeout(5)
    void carriesTheAuthoritiesFromTheEntity() {
        UserModel model = UserModel.builder().username("боец").build();
        model.addAuthority("USER");
        assertThat("the role prefix is lost on the way to the model",
            model.getAuthorities(), contains("ROLE_USER"));
    }

    @DisplayName("Сущность доносит имя до модели")
    @Test
    @Timeout(5)
    void carriesTheNameFromTheEntity() {
        UserEntity entity = UserEntity.builder()
            .id(UUID.randomUUID()).username("боец").email("боец@example.com")
            .password("пароль").authorities(List.of()).enabled(true).build();
        assertThat("the name is lost between the entity and the model",
            UserMapper.MAPPER.userEntityToUserModel(entity).getUsername(), is("боец"));
    }
}
