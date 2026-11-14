package ru.mifi.practice.vol4.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Учебная реализация HMAC из RFC 2104 с SHA-256.
 */
public final class HmacSha256 {
    private static final int BLOCK_SIZE = 64;
    private static final int INNER_PAD = 0x36;
    private static final int OUTER_PAD = 0x5c;

    private HmacSha256() {
    }

    public static byte[] tag(byte[] key, byte[] message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] block = keyBlock(key, sha256);

        // digest завершает внутренний хеш и сбрасывает состояние SHA-256.
        sha256.update(mask(block, INNER_PAD));
        byte[] innerHash = sha256.digest(message);

        sha256.update(mask(block, OUTER_PAD));
        return sha256.digest(innerHash);
    }

    public static boolean verify(byte[] key, byte[] message, byte[] receivedTag) throws NoSuchAlgorithmException {
        return MessageDigest.isEqual(tag(key, message), receivedTag);
    }

    private static byte[] keyBlock(byte[] key, MessageDigest sha256) {
        byte[] material = key.length > BLOCK_SIZE ? sha256.digest(key) : key;
        return Arrays.copyOf(material, BLOCK_SIZE);
    }

    private static byte[] mask(byte[] block, int pad) {
        byte[] result = new byte[block.length];
        for (int index = 0; index < block.length; index++) {
            result[index] = (byte) (block[index] ^ pad);
        }
        return result;
    }
}
