package ru.mifi.practice.vol4.hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("HMAC-SHA-256")
final class HmacSha256Test {
    private static final byte[] MESSAGE = "Hi There".getBytes(StandardCharsets.UTF_8);

    @Test
    @DisplayName("Совпадает с вектором RFC 4231 №1")
    void matchesShortKeyVector() throws GeneralSecurityException {
        byte[] expected = HexFormat.of().parseHex(
            "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7");
        assertArrayEquals(expected, HmacSha256.tag(repeatedByte(20, 0x0b), MESSAGE));
    }

    @Test
    @DisplayName("Совпадает с вектором RFC 4231 №6 для длинного ключа")
    void matchesLongKeyVector() throws GeneralSecurityException {
        byte[] message = "Test Using Larger Than Block-Size Key - Hash Key First"
            .getBytes(StandardCharsets.UTF_8);
        byte[] expected = HexFormat.of().parseHex(
            "60e431591ee0b67f0d8a26aacbf5b77f8e0bc6213728c5140546040f0ee37f54");
        assertArrayEquals(expected, HmacSha256.tag(repeatedByte(131, 0xaa), message));
    }

    @ParameterizedTest
    @ValueSource(ints = {64, 65})
    @DisplayName("Совпадает с JCA на границе размера блока")
    void matchesJcaAtBlockBoundary(int keyLength) throws GeneralSecurityException {
        byte[] key = repeatedByte(keyLength, 0xaa);
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        assertArrayEquals(mac.doFinal(MESSAGE), HmacSha256.tag(key, MESSAGE));
    }

    @Test
    @DisplayName("Принимает исходное сообщение и отклоняет изменённое со старым тегом")
    void rejectsChangedMessage() throws GeneralSecurityException {
        byte[] key = repeatedByte(20, 0x0b);
        byte[] tag = HmacSha256.tag(key, MESSAGE);
        assertTrue(HmacSha256.verify(key, MESSAGE, tag));
        assertFalse(HmacSha256.verify(key, "Hi there".getBytes(StandardCharsets.UTF_8), tag));
        assertFalse(HmacSha256.verify(key, MESSAGE, Arrays.copyOf(tag, tag.length - 1)));
    }

    private static byte[] repeatedByte(int length, int value) {
        byte[] bytes = new byte[length];
        Arrays.fill(bytes, (byte) value);
        return bytes;
    }
}
