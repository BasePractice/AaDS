package ru.mifi.practice.vol8.otp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Ключи контекста OTP")
final class OTPKeyTest {

    @DisplayName("Каждый ключ занимает собственную ячейку контекста")
    @Test
    @Timeout(5)
    void givesEveryKeyItsOwnCell() {
        assertThat("two keys share a single cell of the context",
            Arrays.stream(OTPKey.values()).map(OTPKey::key).collect(Collectors.toSet()).size(),
            is(OTPKey.values().length));
    }

    @DisplayName("Счётчик отправок отделён от счётчика проверок")
    @Test
    @Timeout(5)
    void separatesSendAttemptsFromVerificationAttempts() {
        assertThat("incrementing the send counter also moves the verification counter",
            OTPKey.SEND_CODE_ATTEMPTS.key().equals(OTPKey.VERIFICATION_CODE_ATTEMPTS.key()), is(false));
    }

    @DisplayName("Лимит отправок отделён от лимита проверок")
    @Test
    @Timeout(5)
    void separatesSendLimitFromVerificationLimit() {
        assertThat("the two limits share a single cell",
            OTPKey.MAX_SEND_CODE_ATTEMPTS.key().equals(OTPKey.MAX_VERIFICATION_CODE_ATTEMPTS.key()), is(false));
    }
}
