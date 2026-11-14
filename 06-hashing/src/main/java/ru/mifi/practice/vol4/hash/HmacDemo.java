package ru.mifi.practice.vol4.hash;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

public final class HmacDemo {
    private HmacDemo() {
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        byte[] key = new byte[20];
        Arrays.fill(key, (byte) 0x0b);
        byte[] original = "Hi There".getBytes(StandardCharsets.UTF_8);
        byte[] receivedTag = HmacSha256.tag(key, original);

        byte[] changed = "Hi there".getBytes(StandardCharsets.UTF_8);
        System.out.println("Тег: " + HexFormat.of().formatHex(receivedTag));
        System.out.println("Hi There: " + HmacSha256.verify(key, original, receivedTag));
        System.out.println("Hi there: " + HmacSha256.verify(key, changed, receivedTag));
    }
}
