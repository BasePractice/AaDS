package ru.mifi.practice.voln.prime;

import lombok.Getter;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

@Getter
public final class DiffieHellman {
    private final BigInteger prime; // большое простое число p
    private final BigInteger generator; // первообразный корень g
    private final BigInteger privateKey;
    private final BigInteger publicKey;
    private BigInteger sharedSecret;

    public DiffieHellman(BigInteger prime, BigInteger generator) {
        Random random = new SecureRandom();
        this.prime = prime;
        this.generator = generator;
        this.privateKey = generatePrivateKey(prime, random);
        this.publicKey = generatePublicKey(prime, generator, privateKey);
    }

    @SuppressWarnings("unused")
    public DiffieHellman(BigInteger prime, BigInteger generator, BigInteger privateKey) {
        this.prime = prime;
        this.generator = generator;
        this.privateKey = privateKey;
        this.publicKey = generatePublicKey(prime, generator, privateKey);
    }

    public DiffieHellman(int bitLength) {
        Random random = new SecureRandom();
        this.prime = BigInteger.probablePrime(bitLength, random);
        this.generator = new BigInteger(bitLength - 1, random)
            .mod(prime.subtract(BigInteger.ONE))
            .add(BigInteger.ONE);
        this.privateKey = generatePrivateKey(prime, random);
        this.publicKey = generatePublicKey(prime, generator, privateKey);
    }

    private static BigInteger generatePublicKey(BigInteger prime, BigInteger generator, BigInteger privateKey) {
        return generator.modPow(privateKey, prime);
    }

    private static BigInteger generatePrivateKey(BigInteger prime, Random random) {
        return new BigInteger(prime.bitLength() - 1, random)
            .mod(prime.subtract(BigInteger.TWO))
            .add(BigInteger.ONE);
    }

    public static String toHex(BigInteger value) {
        return Objects.requireNonNull(value).toString(16);
    }

    public static BigInteger fromHex(String hex) {
        return new BigInteger(Objects.requireNonNull(hex), 16);
    }

    public static void writeKey(Path path, BigInteger value) throws IOException {
        String hex = toHex(value);
        Files.writeString(path, hex);
    }

    public static BigInteger readKey(Path path) throws IOException {
        String hex = Files.readString(path, StandardCharsets.UTF_8).trim();
        return fromHex(hex);
    }

    public void calculateSharedSecret(BigInteger otherPublicKey) {
        // sharedSecret = otherPublicKey^privateKey mod prime
        this.sharedSecret = otherPublicKey.modPow(privateKey, prime);
    }

    public String privateKeyHex() {
        return toHex(privateKey);
    }

    public String publicKeyHex() {
        return toHex(publicKey);
    }

    public String sharedSecretHex() {
        return toHex(sharedSecret);
    }

    public void writePrivateKey(Path path) throws IOException {
        writeKey(path, privateKey);
    }

    public void writePublicKey(Path path) throws IOException {
        writeKey(path, publicKey);
    }

    public void writeSharedSecret(Path path) throws IOException {
        writeKey(path, sharedSecret);
    }
}
