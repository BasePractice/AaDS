package ru.mifi.practice.voln.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.domain.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Проверка выпуска и разбора JWT-токенов. */
@DisplayName("Токены доступа")
final class JwtServiceTest {

    @DisplayName("Имя пользователя возвращается из выпущенного токена")
    @Test
    @Timeout(5)
    void givesBackTheUserName() {
        JwtService jwt = new JwtService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        UserEntity user = user("боец");
        assertThat("the user name is lost on the way through the token",
            jwt.extractUserName(jwt.generateToken(user)), is("боец"));
    }

    @DisplayName("Идентификатор пользователя возвращается из выпущенного токена")
    @Test
    @Timeout(5)
    void givesBackTheUserId() {
        JwtService jwt = new JwtService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        UserEntity user = user("боец");
        assertThat("the user id is lost on the way through the token",
            jwt.extractUserId(jwt.generateToken(user)), is(Optional.of(user.getId())));
    }

    @DisplayName("Свежий токен принадлежит тому, кому выпущен")
    @Test
    @Timeout(5)
    void acceptsTheTokenOfItsOwner() {
        JwtService jwt = new JwtService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        UserEntity user = user("боец");
        assertThat("the fresh token is refused for its own owner",
            jwt.isTokenValid(jwt.generateToken(user), user), is(true));
    }

    @DisplayName("Чужой токен не подходит другому пользователю")
    @Test
    @Timeout(5)
    void refusesTheTokenOfSomebodyElse() {
        JwtService jwt = new JwtService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        assertThat("the token of one user passes for another",
            jwt.isTokenValid(jwt.generateToken(user("боец")), user("самозванец")), is(false));
    }

    @DisplayName("Токен, подписанный другим ключом, отвергается")
    @Test
    @Timeout(5)
    void refusesTheForeignSignature() {
        String token = new JwtService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=").generateToken(user("боец"));
        JwtService stranger = new JwtService("ZmVkY2JhOTg3NjU0MzIxMGZlZGNiYTk4NzY1NDMyMTA=");
        assertThrows(JwtException.class, () -> stranger.extractUserName(token),
            "a token signed with another key passes the check");
    }

    private static UserEntity user(String username) {
        return UserEntity.builder()
            .id(UUID.randomUUID())
            .username(username)
            .email(username + "@example.com")
            .password("пароль")
            .authorities(List.of())
            .enabled(true)
            .build();
    }
}
